package org.yurusanp.meskit.symtab

import java.util.concurrent.atomic.AtomicInteger

/**
 * A symbol manager used by all scopes.
 */
class SymMan {
  /**
   * Current scope.
   */
  var curScope = Scope(null, this)

  /**
   * Inverse mappings from inner names to surface symbols.
   */
  val inverses: MutableMap<String, String> = mutableMapOf()

  /**
   * For selector inner names, we also wish to store their corresponding constructor inner names.
   */
  val selsToCtors: MutableMap<String, String> = mutableMapOf()

  // the symbol count shared by all scopes
  private val symCnt = AtomicInteger(0)

  /**
   * Generates a fresh inner name.
   */
  fun genInner() = "MES__${symCnt.getAndIncrement()}"

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
   * Takes a snapshot of the current symbol manager.
   */
  fun snapshot(): SymMan = SymMan().also { newSymMan ->
    val newCurScope = curScope.snapshot(newSymMan)
    newSymMan.curScope = newCurScope
    newSymMan.inverses.putAll(inverses)
    newSymMan.selsToCtors.putAll(selsToCtors)
    newSymMan.symCnt.set(symCnt.get())
  }
}
