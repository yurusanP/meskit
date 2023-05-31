package org.yurusanp.musket

import org.yurusanp.meskit.parser.SemGuSBaseVisitor
import org.yurusanp.meskit.parser.SemGuSParser.DeclareTermTypesCommandContext
import org.yurusanp.meskit.parser.SemGuSParser.SortDecContext
import org.yurusanp.meskit.util.normalize
import org.yurusanp.musket.symtab.SymMan

private class SyntaxProviderState(val symMan: SymMan = SymMan()) {
  /**
   * Returns a snapshot of the current state.
   */
  fun snapshot(): SyntaxProviderState = SyntaxProviderState(symMan.snapshot())
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
    states.addFirst(SyntaxProviderState())
  }

  private fun popState() {
    states.removeFirst()
  }

  override fun visitDeclareTermTypesCommand(ctx: DeclareTermTypesCommandContext) {
    val sortDecCtxs: MutableList<SortDecContext> = ctx.sortDec()
    for (sortDecCtx in sortDecCtxs) {
      val sortSym: String = sortDecCtx.symbol().normalize()
      st.symMan.curScope.insert(sortSym)
    }
  }
}
