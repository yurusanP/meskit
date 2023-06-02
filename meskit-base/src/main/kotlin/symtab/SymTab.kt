package org.yurusanp.meskit.symtab

/**
 * A wrapper around a mutable map from surface symbols to their information,
 */
class SymTab {
  private val mappings: MutableMap<String, SymTabEntry> = mutableMapOf()

  fun lookup(sym: String): SymTabEntry? = mappings[sym]

  fun insert(sym: String, entry: SymTabEntry) {
    mappings.put(sym, entry)?.let { error("Cannot insert symbol $sym.") }
  }

  fun insertAll(other: SymTab) {
    mappings.putAll(
      other.mappings.mapValues { (_, entry) ->
        entry.snapshot()
      },
    )
  }
}

/**
 * Contains a unique inner name of the symbol, and
 * optionally a symbol table for its selectors if it is a constructor symbol.
 *
 * NOTE: shall be careful since building AST nodes doesn't require checking types
 */
class SymTabEntry(val inner: String, val selSymTab: SymTab?) {
  /**
   * Takes a snapshot of the symbol table entry.
   */
  fun snapshot(): SymTabEntry = SymTabEntry(
    inner,
    selSymTab?.let { selEntry -> SymTab().apply { insertAll(selEntry) } },
  )
}
