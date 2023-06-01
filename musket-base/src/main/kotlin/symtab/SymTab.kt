package org.yurusanp.musket.symtab

/**
 * A wrapper around a mutable map from surface symbols to their information,
 * with an inverse mapping from unique inner representations to surface symbols.
 */
class SymTab {
  private val mappings: MutableMap<String, SymTabEntry> = mutableMapOf()
  private val inverses: MutableMap<String, String> = mutableMapOf()

  fun lookup(sym: String): SymTabEntry? = mappings[sym]

  fun inverse(inner: String): String? = inverses[inner]

  fun insert(sym: String, entry: SymTabEntry) {
    mappings.put(sym, entry)?.let { error("Cannot insert symbol $sym.") }
    inverses.put(entry.inner, sym)?.let { error("Cannot insert inner representation ${entry.inner}.") }
  }

  fun putAll(other: SymTab) {
    mappings.putAll(other.mappings)
  }
}

/**
 * Contains a unique inner representation of the symbol, and
 * optionally a list for its fields inner representations if it is a constructor (i.e., struct) symbol.
 *
 * NOTE: shall be careful since building AST nodes doesn't require checking types
 */
data class SymTabEntry(val inner: String, val fieldInners: List<String>?)
