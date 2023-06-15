package org.yurusanp.meskit.surface

import kotlinx.serialization.Serializable
import org.yurusanp.meskit.symtab.Inner

@Serializable
sealed interface AttrVal : Representation {
  @Serializable
  data object Unit

  @Serializable
  data class Literal(val specConst: SpecConst) : AttrVal

  @Serializable
  data class Symbol(val name: Inner) : AttrVal

  @Serializable
  data class Composite(val attrVals: List<AttrVal>) : AttrVal
}
