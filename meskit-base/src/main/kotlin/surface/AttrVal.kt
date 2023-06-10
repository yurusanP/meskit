package org.yurusanp.meskit.surface

sealed interface AttrVal : Representation {
  data object Unit

  data class Literal(val specConst: SpecConst) : AttrVal

  data class Inner(val inner: String) : AttrVal

  data class Composite(val attrVals: List<AttrVal>) : AttrVal
}
