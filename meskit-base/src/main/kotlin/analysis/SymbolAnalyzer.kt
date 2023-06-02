package org.yurusanp.meskit.analysis

import org.yurusanp.meskit.parser.SemGuSParser.*

/**
 * Normalizes a symbol, removing sticks that surrounds the quoted symbol.
 *
 * NOTE: a quoted symbol can contain whitespace characters, including newlines.
 */
fun SymbolContext.analyze(): String = childTerminalNode(0).let {
  when (it.symbol.type) {
    SimpleSymbol -> it.text
    QuotedSymbol -> it.text.substring(1, it.text.length - 1)
    else -> throw GrammarMatchException()
  }
}
