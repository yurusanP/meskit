package org.yurusanp.meskit.symtab

/**
 * A lexical scope with a symbol table,
 * Including the mappings from surface symbols to unique inner names,
 * and the mappings from struct (i.e., ctor) surface symbols to their symbol tables for fields.
 */
class Scope(val parent: Scope?, private val symMan: SymMan) {
  private val symTab: SymTab = SymTab()

  /**
   * Looks up a symbol in the scope and its ancestors.
   *
   * TODO: sepearate namespaces
   */
  fun lookup(sym: String): SymTabEntry = symTab.lookup(sym)
    ?: parent?.lookup(sym)
    ?: error("Cannot find symbol $sym.")

  /**
   * Inserts a symbol into the scope,
   * optionally providing a symbol table for its selectors if it is a constructor symbol.
   */
  fun insert(sym: String, selSymTab: SymTab? = null): SymTabEntry = let {
    val freshInner: String = symMan.genInner()
    val entry = SymTabEntry(freshInner, selSymTab)
    symTab.insert(sym, entry)
    // also create an inverse mapping
    symMan.inverses.put(freshInner, sym)?.let { error("Duplicate inner name $freshInner.") }
    entry
  }

  /**
   * Takes a snapshot of the scope with its ancestors.
   */
  fun snapshot(newSymMan: SymMan): Scope = Scope(parent?.snapshot(newSymMan), newSymMan).also { newScope ->
    newScope.symTab.insertAll(symTab)
  }
}
