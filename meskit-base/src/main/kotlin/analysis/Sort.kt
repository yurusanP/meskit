package org.yurusanp.meskit.analysis

sealed interface Sort : Representation {
  data class Simple(val ident: Ident) : Sort

  data class Par(val ident: Ident, val params: Sort) : Sort
}