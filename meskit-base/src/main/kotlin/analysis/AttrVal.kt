package org.yurusanp.meskit.analysis

sealed interface AttrVal : Representation {
  data object Unit

  data class Literal(val specConst: SpecConst) : AttrVal

  // NOTE: only for referencing
  data class Inner(val inner: String) : AttrVal

  data class Composite(val attrVals: List<AttrVal>) : AttrVal
}
