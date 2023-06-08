package org.yurusanp.meskit.analysis

// TODO: process qual in type checking
sealed interface Ident : Representation {
  data class Inner(val inner: String) : Ident

  data class Indexed(val inner: String, val indices: List<Index>) : Ident
}
