package org.yurusanp.meskit.surface

sealed interface SpecConst : Representation {
  data class Num(val value: Int) : SpecConst

  data class Dec(val value: Double) : SpecConst

  data class Hex(val value: String) : SpecConst

  data class Bin(val value: String) : SpecConst

  data class Str(val value: String) : SpecConst
}
