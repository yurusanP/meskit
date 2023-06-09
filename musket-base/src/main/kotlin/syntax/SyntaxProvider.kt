package org.yurusanp.musket.syntax

import org.yurusanp.meskit.analysis.Representation
import org.yurusanp.meskit.parser.SemGuSBaseVisitor
import org.yurusanp.meskit.parser.SemGuSParser.*
import org.yurusanp.musket.translate.trans

/**
 * Provides an AST for the Sketch language, assuming that the input is resolved and well-typed.
 */
class SyntaxProvider : SemGuSBaseVisitor<Unit>() {
  // stack of states
  private val states: ArrayDeque<SyntaxProviderState> = ArrayDeque(listOf(SyntaxProviderState()))

  // current state
  val st: SyntaxProviderState
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
    val mesTermTypeDefs: List<Representation.TermTypeDef> = st.analyzer.visitDeclareTermTypesCommand(ctx).reps
    st.adTypeDefs += mesTermTypeDefs.map(Representation.TermTypeDef::trans)
  }

  // TODO: support functions that are not semantic relations
  override fun visitDefineFunsRecCommand(ctx: DefineFunsRecCommandContext) {
    super.visitDefineFunsRecCommand(ctx)
  }
}
