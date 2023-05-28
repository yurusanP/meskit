package org.yurusanp.meskit

import org.yurusanp.meskit.parser.SemGuSBaseVisitor
import org.yurusanp.meskit.parser.SemGuSParser.*

open class BaseInterpreter : SemGuSBaseVisitor<Unit>() {
  open inner class BaseInterpreterContext {}

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
    TODO("Not yet implemented")
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
