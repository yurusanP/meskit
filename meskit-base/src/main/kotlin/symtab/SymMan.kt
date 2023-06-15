package org.yurusanp.meskit.symtab

import java.util.concurrent.atomic.AtomicInteger

// TODO: perhaps make the symbol manager more general?

/**
 * A symbol manager used by all scopes.
 */
class SymMan(val namespaces: List<String>, theoryLoader: Scope.() -> Unit = {}) {
  // the symbol count shared by all scopes
  private val genCnt = AtomicInteger(0)

  /**
   * Inverse mappings from inner names to surface symbols.
   */
  val inverses: MutableMap<Inner, String> = mutableMapOf()

  /**
   * Current scope.
   */
  // NOTE: goes after the above definitions to avoid initialization problem
  var curScope: Scope = Scope(null, this).apply(theoryLoader)

  /**
   * For selector inner names, we also wish to store their corresponding constructor inner names.
   */
  // TODO: shouldn't I move this into the analyzer?
  // val selsToCtors: MutableMap<String, String> = mutableMapOf()

  /**
   * Generates a fresh inner name.
   */
  fun genInner(): Inner = Inner("MES__${genCnt.getAndIncrement()}")

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
  fun snapshot(): SymMan = SymMan(namespaces).also { newSymMan ->
    val newCurScope = curScope.snapshot(newSymMan)
    newSymMan.curScope = newCurScope
    newSymMan.inverses.putAll(inverses)
    newSymMan.genCnt.set(genCnt.get())
  }
}
