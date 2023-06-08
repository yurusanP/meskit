package org.yurusanp.meskit.analysis

sealed interface Term : Representation {
  val attr: Map<String, AttrVal>?

  data class Literal(val specConst: SpecConst, override val attr: Map<String, AttrVal>? = null) : Term

  // TODO: deal with qual later
  data class Ref(val ident: Ident, override val attr: Map<String, AttrVal>? = null) : Term

  data class App(val ident: Ident, val args: List<Term>, override val attr: Map<String, AttrVal>? = null) : Term

  data class Let(val bindings: List<Representation.Binding>, val body: Term, override val attr: Map<String, AttrVal>? = null) : Term

  data class Forall(val sortedInners: List<Representation.SortedInner>, val body: Term, override val attr: Map<String, AttrVal>? = null) : Term

  data class Exists(val sortedInners: List<Representation.SortedInner>, val body: Term, override val attr: Map<String, AttrVal>? = null) : Term

  data class Match(val term: Term, val cases: List<Representation.Case>, override val attr: Map<String, AttrVal>? = null) : Term
}
