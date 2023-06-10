package org.yurusanp.meskit.surface

import kotlinx.serialization.Serializable

/**
 * Representation of meskit resolver result.
 */
@Serializable
sealed interface Representation {
  // function definition

  @Serializable
  data class FunDef(val funDec: FunDec, val body: Term) : Representation

  @Serializable
  data class FunDec(val inner: String, val params: List<SortedInner>, val retSort: Sort) : Representation

  // term

  @Serializable
  data class Binding(val inner: String, val term: Term) : Representation

  @Serializable
  data class SortedInner(val sort: Sort, val inner: String) : Representation

  @Serializable
  data class Case(val inner: String, val params: List<String>, val body: Term) : Representation

  // term types

  @Serializable
  data class TermTypeDef(val sortDec: SortDec, val ctors: List<Ctor>) : Representation

  @Serializable
  data class Ctor(val inner: String, val sels: List<SelDec>) : Representation

  @Serializable
  data class SortDec(val inner: String, val arity: Int) : Representation

  // TODO: Modify sortInner to sort
  @Serializable
  data class SelDec(val sortInner: String, val inner: String) : Representation
}
