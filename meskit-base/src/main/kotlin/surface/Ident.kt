package org.yurusanp.meskit.surface

import kotlinx.serialization.Serializable

// TODO: process qual in type checking
@Serializable
sealed interface Ident : Representation {
  @Serializable
  data class Inner(val inner: String) : Ident

  @Serializable
  data class Indexed(val inner: String, val indices: List<Index>) : Ident
}
