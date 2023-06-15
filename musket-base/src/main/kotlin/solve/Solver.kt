package org.yurusanp.musket.solve

import org.yurusanp.meskit.parser.SemGuSBaseVisitor
import org.yurusanp.meskit.parser.SemGuSParser.*
import org.yurusanp.meskit.surface.Def
import org.yurusanp.meskit.surface.Representation
import org.yurusanp.musket.syntax.Ann
import org.yurusanp.musket.syntax.Node
import org.yurusanp.musket.syntax.Stmt

class Solver : SemGuSBaseVisitor<Unit>() {
  // stack of states
  private val states: ArrayDeque<SolverState> = ArrayDeque(listOf(SolverState()))

  // current state
  val st: SolverState
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
    val mesTermTypeDefs: List<Def.SortFam> = st.resolver.visitDeclareTermTypesCommand(ctx).reps
    st.adTypeDefs += mesTermTypeDefs.map(::transTermTypeDef)
  }

  private fun transTermTypeDef(mesTermTypeDef: Def.SortFam): Stmt.ADTypeDef = Stmt.ADTypeDef(
    Node.ADType(
      mesTermTypeDef.name.value,
      mesTermTypeDef.ctors.map(::transTermTypeCtor),
      null,
    ),
  )

  private fun transTermTypeCtor(mesCtor: Representation.Ctor): Node.Ctor = Node.Ctor(
    mesCtor.name.value,
    mesCtor.sels.map(::transTermTypeSel),
  )

  private fun transTermTypeSel(mesSel: Representation.SortedInner): Stmt.VarDec = Stmt.VarDec(
    Node.AnnedSym(
      Ann.DType(mesSel.sort.indexed.name.value),
      mesSel.name.value,
    ),
  )

  // TODO: support functions that are not semantic relations
  override fun visitDefineFunsRecCommand(ctx: DefineFunsRecCommandContext) {
    super.visitDefineFunsRecCommand(ctx)
  }
}
