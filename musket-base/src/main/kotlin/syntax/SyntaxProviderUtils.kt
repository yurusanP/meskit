package org.yurusanp.musket.syntax

import org.yurusanp.meskit.analysis.Analyzer

class SyntaxProviderState(val analyzer: Analyzer = Analyzer()) {
  val adTypeDefs: MutableList<Stmt.ADTypeDef> = mutableListOf()

  /**
   * Takes a snapshot of the current state.
   */
  fun snapshot(): SyntaxProviderState = SyntaxProviderState(Analyzer(analyzer.st.snapshot())).also { newSt ->
    // each node is immutable, so we can just copy the references
    newSt.adTypeDefs.addAll(adTypeDefs)
  }
}
