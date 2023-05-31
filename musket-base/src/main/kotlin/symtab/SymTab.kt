package org.yurusanp.musket.symtab

/**
 * A wrapper around a mutable map from surface symbols to their information.
 */
class SymTab {
  private val mappings: MutableMap<String, SymTabEntry> = mutableMapOf()

  fun lookup(sym: String): SymTabEntry? = mappings[sym]

  fun insert(sym: String, entry: SymTabEntry) {
    mappings.put(sym, entry)?.let { error("Cannot insert symbol $sym.") }
  }

  fun putAll(other: SymTab) {
    mappings.putAll(other.mappings)
  }
}

/**
 * Contains a unique inner representation of the symbol, and
 * optionally a symbol table for its fields if it is a constructor (i.e., struct) symbol.
 *
 * NOTE: shall be careful since building AST nodes doesn't require checking types
 */
data class SymTabEntry(val inner: String, val fieldSymTab: SymTab?)
