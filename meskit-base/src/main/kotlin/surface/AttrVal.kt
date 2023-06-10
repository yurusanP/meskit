package org.yurusanp.meskit.surface

import kotlinx.serialization.Serializable

@Serializable
sealed interface AttrVal : Representation {
  @Serializable
  data object Unit

  @Serializable
  data class Literal(val specConst: SpecConst) : AttrVal

  @Serializable
  data class Inner(val inner: String) : AttrVal

  @Serializable
  data class Composite(val attrVals: List<AttrVal>) : AttrVal
}
