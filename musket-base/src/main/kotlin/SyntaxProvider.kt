package org.yurusanp.musket

import org.yurusanp.meskit.parser.SemGuSBaseVisitor
import org.yurusanp.meskit.parser.SemGuSParser.DeclareTermTypesCommandContext
import org.yurusanp.meskit.parser.SemGuSParser.SymbolContext
import org.yurusanp.meskit.util.normalize
import org.yurusanp.musket.symtab.SymMan
import org.yurusanp.musket.syntax.Ann
import org.yurusanp.musket.syntax.Node
import org.yurusanp.musket.syntax.Stmt

private class SyntaxProviderState(val symMan: SymMan = SymMan()) {
  val adTypeDefs: MutableList<Stmt.ADTypeDef> = mutableListOf()

  /**
   * Returns a snapshot of the current state.
   */
  fun snapshot(): SyntaxProviderState = SyntaxProviderState(symMan.snapshot()).also { newSt ->
    // each node is immutable, so we can just copy the references
    newSt.adTypeDefs.addAll(adTypeDefs)
  }
}

/**
 * Provides an AST for the Sketch language, assuming that the input is resolved and well-typed.
 */
class SyntaxProvider : SemGuSBaseVisitor<Unit>() {
  // stack of states
  private val states: ArrayDeque<SyntaxProviderState> = ArrayDeque(listOf(SyntaxProviderState()))

  // current state
  private val st: SyntaxProviderState
    get() = states.first()

  // state pushing and popping happens when changing assertion levels

  private fun pushState() {
    states.addFirst(st.snapshot())
  }

  private fun popState() {
    states.removeFirst()
  }

  override fun visitDeclareTermTypesCommand(ctx: DeclareTermTypesCommandContext) {
    val sortInners: List<String> = ctx.sortDec().asSequence().map { sortDecCtx ->
      val sortSym: String = sortDecCtx.symbol().normalize()
      st.symMan.curScope.insert(sortSym).inner
    }.toList()

    st.adTypeDefs += ctx.termTypeDec().asSequence().zip(sortInners.asSequence()).map { (termTypeDecCtx, inner) ->
      val ctors: List<Node.Ctor> = termTypeDecCtx.termDec().asSequence().map { termDecCtx ->
        val ctorComponents: List<String> = termDecCtx.symbol().map(SymbolContext::normalize).toList()
        val fieldInners = (1 until ctorComponents.size).map { st.symMan.gensym() }
        val (ctorInner) = st.symMan.curScope.insert(ctorComponents.first(), fieldInners)
        Node.Ctor(
          ctorInner,
          ctorComponents.asSequence().drop(1).mapIndexed { i, fieldDType ->
            Stmt.VarDec(Node.AnnSym(Ann.DType(st.symMan.curScope.lookup(fieldDType).inner), fieldInners[i]))
          }.toList(),
        )
      }.toList()

      Node.ADType(
        inner,
        ctors,
        null,
      )
    }.map(Stmt::ADTypeDef)
  }
}
