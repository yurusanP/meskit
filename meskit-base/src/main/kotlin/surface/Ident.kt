package org.yurusanp.meskit.surface

import kotlinx.serialization.Serializable

// NOTE: there is no user-defined symbol-indexed identifier in SMT-LIB theories
@Serializable
sealed interface Ident : Representation {
  // according to SMT-LIB specification, when calling a parameterized function,
  // the return sort must be specified
  @Serializable
  data class Fun(val indexed: Representation.IndexedInner, val qual: Sort? = null) : Ident

  @Serializable
  data class Sort(val indexed: Representation.IndexedInner, val args: List<Sort>? = null) : Ident
}
