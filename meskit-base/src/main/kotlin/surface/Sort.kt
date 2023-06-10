package org.yurusanp.meskit.surface

import kotlinx.serialization.Serializable

@Serializable
sealed interface Sort : Representation {
  @Serializable
  data class Simple(val ident: Ident) : Sort

  @Serializable
  data class Par(val ident: Ident, val params: List<Sort>) : Sort
}
