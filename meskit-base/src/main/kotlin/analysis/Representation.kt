package org.yurusanp.meskit.analysis

/**
 * Representation of meskit analysis result.
 */
sealed interface Representation {
  // function definition

  data class FunDef(val funDec: FunDec, val body: Term) : Representation

  data class FunDec(val inner: String, val params: List<SortedInner>, val retSort: Sort) : Representation

  // term

  data class Binding(val inner: String, val term: Term) : Representation

  data class SortedInner(val sort: Sort, val inner: String) : Representation

  data class Case(val inner: String, val params: List<String>, val body: Term) : Representation

  // term types

  data class TermTypeDef(val sortDec: SortDec, val ctors: List<Ctor>) : Representation

  data class Ctor(val inner: String, val sels: List<SelDec>) : Representation

  data class SortDec(val inner: String, val arity: Int) : Representation

  // TODO: Modify sortInner to sort
  data class SelDec(val sortInner: String, val inner: String) : Representation
}
