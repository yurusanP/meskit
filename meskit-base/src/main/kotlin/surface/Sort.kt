package org.yurusanp.meskit.surface

sealed interface Sort : Representation {
  data class Simple(val ident: Ident) : Sort

  data class Par(val ident: Ident, val params: List<Sort>) : Sort
}
