package org.yurusanp.meskit.util

import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.TerminalNode
import org.yurusanp.meskit.parser.SemGuSParser
import org.yurusanp.meskit.parser.SemGuSParser.*

class GrammarMatchException : IllegalStateException("Unhandled grammar match")

fun ParserRuleContext.childTerminalNode(i: Int) = (this.getChild(i) as TerminalNode)

/**
 * Normalizes a symbol, removing sticks that surrounds the quoted symbol.
 *
 * NOTE: a quoted symbol can contain whitespace characters, including newlines.
 */
fun SemGuSParser.SymbolContext.normalize(): String = childTerminalNode(0).let {
  when (it.symbol.type) {
    SimpleSymbol -> it.text
    QuotedSymbol -> it.text.substring(1, it.text.length - 1)
    else -> throw GrammarMatchException()
  }
}
