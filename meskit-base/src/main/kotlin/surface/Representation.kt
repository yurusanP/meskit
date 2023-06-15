package org.yurusanp.meskit.surface

import kotlinx.serialization.Serializable
import org.yurusanp.meskit.symtab.Inner

/**
 * Representation of meskit resolver result.
 */
@Serializable
sealed interface Representation {
  // ident

  @Serializable
  data class IndexedInner(val name: Inner, val indices: List<Index>? = null) : Representation

  // def

  @Serializable
  data class SortedInner(val name: Inner, val sort: Ident.Sort) : Representation

  @Serializable
  data class Lambda(val inputs: List<SortedInner>, val output: Term, val outputSort: Ident.Sort) : Representation

  @Serializable
  data class Ctor(val name: Inner, val sels: List<SortedInner>) : Representation

  // term

  @Serializable
  data class Binding(val name: Inner, val term: Term) : Representation

  @Serializable
  data class Case(val name: Inner, val params: List<Inner>, val body: Term) : Representation
}
