package org.yurusanp.musket.symtab

/**
 * A lexical scope with a symbol table,
 * Including the mappings from surface symbols to unique inner representations,
 * and the mappings from struct (i.e., ctor) surface symbols to their symbol tables for fields.
 */
class Scope(val parent: Scope?, private val symMan: SymMan) {
  private val symTab: SymTab = SymTab()

  /**
   * Looks up a symbol in the scope and its ancestors.
   */
  fun lookup(sym: String): SymTabEntry = symTab.lookup(sym)
    ?: parent?.lookup(sym)
    ?: error("Cannot find symbol $sym.")

  /**
   * Inserts a symbol into the scope,
   * optionally providing a list for its fields inner representations if it is a constructor (i.e., struct) symbol.
   */
  fun insert(sym: String, fieldInners: List<String>? = null): SymTabEntry = let {
    val freshSym = symMan.gensym()
    val entry = SymTabEntry(freshSym, fieldInners)
    symTab.insert(sym, entry)
    entry
  }

  /**
   * Take a snapshot of the scope with its ancestors.
   */
  fun snapshot(newSymMan: SymMan): Scope = Scope(parent?.snapshot(newSymMan), newSymMan).also { newScope ->
    newScope.symTab.putAll(symTab)
  }
}
