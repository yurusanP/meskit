package org.yurusanp.musket

import org.yurusanp.meskit.analysis.AnalyzerState
import org.yurusanp.meskit.analysis.TermType
import org.yurusanp.meskit.parser.SemGuSBaseVisitor
import org.yurusanp.meskit.parser.SemGuSParser.*
import org.yurusanp.meskit.analysis.analyze
import org.yurusanp.musket.syntax.Stmt
import org.yurusanp.musket.translate.trans

private class SyntaxProviderState(val analyzerSt: AnalyzerState = AnalyzerState()) {
  val adTypeDefs: MutableList<Stmt.ADTypeDef> = mutableListOf()

  /**
   * Takes a snapshot of the current state.
   */
  fun snapshot(): SyntaxProviderState = SyntaxProviderState(analyzerSt.snapshot()).also { newSt ->
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

  // visit methods

  override fun visitPushCommand(ctx: PushCommandContext) {
    repeat(ctx.Numeral().symbol.text.toInt()) {
      pushState()
    }
  }

  override fun visitPopCommand(ctx: PopCommandContext) {
    repeat(ctx.Numeral().symbol.text.toInt()) {
      popState()
    }
  }

  override fun visitDeclareTermTypesCommand(ctx: DeclareTermTypesCommandContext) {
    val mesTermTypes: List<TermType> = ctx.analyze(st.analyzerSt)
    st.adTypeDefs += mesTermTypes.map(TermType::trans).map(Stmt::ADTypeDef)
  }
}
