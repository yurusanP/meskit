package org.yurusanp.musket

import org.yurusanp.meskit.parser.SemGuSBaseVisitor
import org.yurusanp.meskit.parser.SemGuSParser.*
import org.yurusanp.meskit.util.sym

open class SyntaxProvider : SemGuSBaseVisitor<Unit>() {
  override fun visitAssumeCommand(ctx: AssumeCommandContext) {
    TODO("Not yet implemented")
  }

  override fun visitCheckSynthCommand(ctx: CheckSynthCommandContext) {
    TODO("Not yet implemented")
  }

  override fun visitConstraintCommand(ctx: ConstraintCommandContext) {
    TODO("Not yet implemented")
  }

  override fun visitDeclareDatatypeCommand(ctx: DeclareDatatypeCommandContext) {
    TODO("Not yet implemented")
  }

  override fun visitDeclareDatatypesCommand(ctx: DeclareDatatypesCommandContext) {
    TODO("Not yet implemented")
  }

  override fun visitDeclareSortCommand(ctx: DeclareSortCommandContext) {
    TODO("Not yet implemented")
  }

  override fun visitDeclareTermTypesCommand(ctx: DeclareTermTypesCommandContext) {
    val childCtxs: List<Pair<SortDecContext, TermTypeDecContext>> = ctx
      .sortDec().asSequence()
      .zip(ctx.termTypeDec().asSequence())
      .toList()

    for ((sortDecCtx, termTypeDecCtx) in childCtxs) {
      val sortSym: String = sortDecCtx.symbol().sym()
      val termDec: List<TermDecContext> = termTypeDecCtx.termDec().toList()

    }
////    val meow = sortDec.symbol()
////    println("meow")
  }

  override fun visitDeclareVarCommand(ctx: DeclareVarCommandContext) {
    TODO("Not yet implemented")
  }

  override fun visitDefineFunCommand(ctx: DefineFunCommandContext) {
    TODO("Not yet implemented")
  }

  override fun visitDefineFunRecCommand(ctx: DefineFunRecCommandContext) {
    TODO("Not yet implemented")
  }

  override fun visitDefineFunsRecCommand(ctx: DefineFunsRecCommandContext) {
    TODO("Not yet implemented")
  }

  override fun visitDefineSortCommand(ctx: DefineSortCommandContext) {
    TODO("Not yet implemented")
  }

  override fun visitPushCommand(ctx: PushCommandContext) {
    TODO("Not yet implemented")
  }

  override fun visitPopCommand(ctx: PopCommandContext) {
    TODO("Not yet implemented")
  }

  override fun visitResetCommand(ctx: ResetCommandContext) {
    TODO("Not yet implemented")
  }

  override fun visitSetInfoCommand(ctx: SetInfoCommandContext) {
    TODO("Not yet implemented")
  }

  override fun visitSetLogicCommand(ctx: SetLogicCommandContext) {
    TODO("Not yet implemented")
  }

  override fun visitSetOptionCommand(ctx: SetOptionCommandContext) {
    TODO("Not yet implemented")
  }

  override fun visitSynthFunCommand(ctx: SynthFunCommandContext) {
    TODO("Not yet implemented")
  }
}
