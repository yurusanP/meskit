package org.yurusanp.meskit.symtab

/**
 * A lexical scope with a symbol table,
 * Including the mappings from surface symbols to unique inner names,
 */
class Scope(val parent: Scope?, private val symMan: SymMan) {
  // separate symbol tables for:  and function (or constructor, selector) symbols
  // - sort symbols
  // - function symbols, including constant, constructor, selector
  // - index symbols
  private val sortSymTab: SymTab = SymTab()
  private val funSymTab: SymTab = SymTab()
  private val indexSymTab: SymTab = SymTab()

  /**
   * Looks up a sort symbol in the scope and its ancestors.
   *
   * TODO: sepearate namespaces
   */
  fun lookupSort(sym: String): String = sortSymTab.lookup(sym)
    ?: parent?.lookupSort(sym)
    ?: error("Cannot find sort symbol $sym.")

  /**
   * Looks up a function symbol in the scope and its ancestors.
   *
   * TODO: sepearate namespaces
   */
  fun lookupFun(sym: String): String = funSymTab.lookup(sym)
    ?: parent?.lookupFun(sym)
    ?: error("Cannot find function symbol $sym.")

  /**
   * Looks up an index symbol in the scope and its ancestors.
   */
  fun lookupIndex(sym: String): String = indexSymTab.lookup(sym)
    ?: parent?.lookupIndex(sym)
    ?: error("Cannot find index symbol $sym.")

  /**
   * Inserts a sort symbol into the scope.
   */
  fun insertSort(sym: String): String = let {
    val freshInner: String = symMan.genInner()
    sortSymTab.insert(sym, freshInner)
    // also create an inverse mapping
    symMan.inverses.put(freshInner, sym)?.let { error("Duplicate inner name $freshInner.") }
    freshInner
  }

  /**
   * Inserts a function symbol into the scope.
   */
  fun insertFun(sym: String?, ctorInner: String? = null): String = let {
    val freshInner: String = symMan.genInner()
    val symNotNull = sym ?: freshInner
    funSymTab.insert(symNotNull, freshInner)
    // also create an inverse mapping
    symMan.inverses.put(freshInner, symNotNull)?.let { error("Duplicate inner name $freshInner.") }
    // also create the mapping to constructor inner name
    ctorInner?.let { ctorInner ->
      symMan.selsToCtors.put(freshInner, ctorInner)?.let { error("Duplicate inner name $freshInner.") }
    }
    freshInner
  }

  /**
   * Inserts an index symbol into the scope.
   */
  fun insertIndex(sym: String): String = let {
    val freshInner: String = symMan.genInner()
    indexSymTab.insert(sym, freshInner)
    // also create an inverse mapping
    symMan.inverses.put(freshInner, sym)?.let { error("Duplicate inner name $freshInner.") }
    freshInner
  }

  /**
   * Takes a snapshot of the scope with its ancestors.
   */
  fun snapshot(newSymMan: SymMan): Scope = Scope(parent?.snapshot(newSymMan), newSymMan).also { newScope ->
    newScope.sortSymTab.insertAll(sortSymTab)
    newScope.funSymTab.insertAll(funSymTab)
    newScope.indexSymTab.insertAll(indexSymTab)
  }
}
