package org.yurusanp.musket.symtab

import java.util.concurrent.atomic.AtomicInteger

/**
 * A symbol manager used by all scopes.
 */
class SymMan {
  var curScope = Scope(null, this)

  // the symbol count shared by all scopes
  private val symCnt = AtomicInteger(0)

  /**
   * Generates a fresh symbol.
   */
  fun gensym() = "MSK__${symCnt.getAndIncrement()}"

  /**
   * Changes to a new scope under the current scope.
   */
  fun pushScope() {
    curScope = Scope(curScope, this)
  }

  /**
   * Changes to the parent scope of the current scope.
   */
  fun popScope() {
    curScope = curScope.parent ?: error("Cannot pop the global scope.")
  }

  // TODO: make more efficient by implementing persistent data structure?

  /**
   * Take a snapshot of the current symbol manager.
   */
  fun snapshot(): SymMan = SymMan().also { newSymMan ->
    val newCurScope = curScope.snapshot(newSymMan)
    newSymMan.curScope = newCurScope
    newSymMan.symCnt.set(symCnt.get())
  }
}
