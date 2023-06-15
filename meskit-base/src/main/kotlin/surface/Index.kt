package org.yurusanp.meskit.surface

import kotlinx.serialization.Serializable

sealed interface Index : Representation {
  @Serializable
  data class Num(val value: Int) : Index

  @Serializable
  data class Str(val value: String) : Index
}
