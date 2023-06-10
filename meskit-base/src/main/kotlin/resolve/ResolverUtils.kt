package org.yurusanp.meskit.resolve

import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.TerminalNode
import org.yurusanp.meskit.parser.SemGuSParser.*
import org.yurusanp.meskit.symtab.SymMan

class ResolverState(val symMan: SymMan = SymMan()) {
  fun snapshot(): ResolverState = ResolverState(symMan.snapshot())

  // TODO: shall I also store type checking info here?
}

sealed interface ResolverResult {
  data class Single<R>(val rep: R) : ResolverResult
  data class Multiple<R>(val reps: List<R>) : ResolverResult
}

class GrammarMatchException : IllegalStateException("Unhandled grammar match")

fun ParserRuleContext.childTerminalNode(i: Int) = (this.getChild(i) as TerminalNode)

/**
 * Normalizes a symbol, removing sticks that surrounds the quoted symbol.
 *
 * NOTE: a quoted symbol can contain whitespace characters, including newlines.
 */
fun SymbolContext.normalize(): String = childTerminalNode(0).let {
  when (it.symbol.type) {
    SimpleSymbol -> it.text
    QuotedSymbol -> it.text.substring(1, it.text.length - 1)
    else -> throw GrammarMatchException()
  }
}
