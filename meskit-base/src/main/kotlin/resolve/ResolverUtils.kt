package org.yurusanp.meskit.resolve

import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.TerminalNode
import org.yurusanp.meskit.prelude.loadTheories
import org.yurusanp.meskit.symtab.Scope
import org.yurusanp.meskit.symtab.SymMan

class ResolverState(val symMan: SymMan = SymMan(listOf("fun", "sort"), Scope::loadTheories)) {
  fun snapshot(): ResolverState = ResolverState(symMan.snapshot())

  // TODO: shall I also store type checking info here?
}

sealed interface ResolverResult {
  data class Single<R>(val rep: R) : ResolverResult
  data class Multiple<R>(val reps: List<R>) : ResolverResult
}

class GrammarMatchException : IllegalStateException("Unhandled grammar match")

class ResolverCheckException(msg: String) : IllegalStateException(msg)

fun ParserRuleContext.childTerminalNode(i: Int) = (this.getChild(i) as TerminalNode)
