package org.yurusanp.musket.solve

import org.yurusanp.meskit.resolve.Resolver
import org.yurusanp.musket.syntax.Stmt

class SolverState(val resolver: Resolver = Resolver()) {
  val adTypeDefs: MutableList<Stmt.ADTypeDef> = mutableListOf()

  /**
   * Takes a snapshot of the current state.
   */
  fun snapshot(): SolverState = SolverState(Resolver(resolver.st.snapshot())).also { newSt ->
    // each node is immutable, so we can just copy the references
    newSt.adTypeDefs.addAll(adTypeDefs)
  }
}
