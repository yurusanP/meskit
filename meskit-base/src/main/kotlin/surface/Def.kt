package org.yurusanp.meskit.surface

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.yurusanp.meskit.symtab.Inner

@Serializable
sealed interface Def : Representation {
  // according to SMT-LIB specification, essentially uses parametric polymorphism to simulate ad-hoc polymorphism
  // indices or sort parameters are just abstract names

  @Serializable
  data class FunFam(
    val name: Inner,
    val lambda: Representation.Lambda,
    val params: List<Inner>? = null,
    @Transient val identCk: (Ident.Fun) -> Unit = {},
  ) : Def

  @Serializable
  data class SortFam(
    val name: Inner,
    val ctors: List<Representation.Ctor>,
    val params: List<Inner>? = null,
    @Transient val identCk: (Ident.Fun) -> Unit = {},
  ) : Def
}
