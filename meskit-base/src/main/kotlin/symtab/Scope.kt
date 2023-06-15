package org.yurusanp.meskit.symtab

/**
 * A lexical scope with a symbol table,
 * Including the mappings from surface symbols to unique inner names,
 */
class Scope(val parent: Scope?, private val symMan: SymMan) {
  private val symTabs: Map<String, SymTab> = symMan.namespaces.associateWith { SymTab() }

  /**
   * Looks up a symbol in the scope and its ancestors.
   */
  fun lookup(key: String, sym: String): Inner = symTabs[key]!!.lookup(sym)
    ?: parent?.lookup(key, sym)
    ?: error("Cannot find $key symbol $sym.")

  fun insert(key: String, sym: String?, fresh: Boolean = true): Inner = let {
    val inner: Inner = if (fresh) symMan.genInner() else Inner(sym ?: error("Cannot use null as inner name."))
    // symNotNull is the inner name if surface symbol is null
    val symNotNull: String = sym ?: inner.value
    symTabs[key]!!.insert(symNotNull, inner)
    // also create an inverse mapping
    symMan.inverses.put(inner, symNotNull)?.let { error("Duplicate inner name $inner.") }
    inner
  }

  /**
   * Takes a snapshot of the scope with its ancestors.
   */
  fun snapshot(newSymMan: SymMan): Scope = Scope(parent?.snapshot(newSymMan), newSymMan).also { newScope ->
    newScope.symTabs.forEach { (key, symTab) ->
      symTab.insertAll(symTabs[key]!!)
    }
  }
}
