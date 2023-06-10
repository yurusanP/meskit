package org.yurusanp.meskit.surface

import kotlinx.serialization.Serializable

@Serializable
sealed interface SpecConst : Representation {
  @Serializable
  data class Num(val value: Int) : SpecConst

  @Serializable
  data class Dec(val value: Double) : SpecConst

  @Serializable
  data class Hex(val value: String) : SpecConst

  @Serializable
  data class Bin(val value: String) : SpecConst

  @Serializable
  data class Str(val value: String) : SpecConst
}
