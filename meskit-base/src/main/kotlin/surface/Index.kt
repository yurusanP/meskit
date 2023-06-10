package org.yurusanp.meskit.surface

import kotlinx.serialization.Serializable

@Serializable
sealed interface Index : Representation {
  // NOTE: not appearing in SMT-LIB defined theories
  // in a user-defined theory, you could have something like (_ move up)
  @Serializable
  data class Inner(val inner: String) : Index

  @Serializable
  data class Num(val num: Int) : Index
}
