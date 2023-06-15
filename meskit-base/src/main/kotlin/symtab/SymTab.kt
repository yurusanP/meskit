package org.yurusanp.meskit.symtab

/**
 * A wrapper around a mutable map from surface symbols to their inner names.
 */
class SymTab {
  private val mappings: MutableMap<String, Inner> = mutableMapOf()

  fun lookup(sym: String): Inner? = mappings[sym]

  fun insert(sym: String, inner: Inner) {
    mappings.put(sym, inner)?.let { error("Cannot insert symbol $sym.") }
  }

  fun insertAll(other: SymTab) {
    mappings.putAll(other.mappings)
  }
}
