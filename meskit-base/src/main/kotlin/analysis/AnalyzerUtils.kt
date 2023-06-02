package org.yurusanp.meskit.analysis

import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.TerminalNode
import org.yurusanp.meskit.symtab.SymMan

class AnalyzerState(val symMan: SymMan = SymMan()) {
  fun snapshot(): AnalyzerState = AnalyzerState(symMan.snapshot())

  // TODO: shall I also store type checking info here?
}

class GrammarMatchException : IllegalStateException("Unhandled grammar match")

fun ParserRuleContext.childTerminalNode(i: Int) = (this.getChild(i) as TerminalNode)
