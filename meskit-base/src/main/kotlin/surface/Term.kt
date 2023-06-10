package org.yurusanp.meskit.surface

sealed interface Term : Representation {
  var attrs: Map<String, AttrVal>?

  data class Literal(val specConst: SpecConst, override var attrs: Map<String, AttrVal>? = null) : Term

  // TODO: deal with qual later
  data class Ref(val ident: Ident, override var attrs: Map<String, AttrVal>? = null) : Term

  data class App(val ident: Ident, val args: List<Term>, override var attrs: Map<String, AttrVal>? = null) : Term

  data class Let(val bindings: List<Representation.Binding>, val body: Term, override var attrs: Map<String, AttrVal>? = null) : Term

  data class Forall(val params: List<Representation.SortedInner>, val body: Term, override var attrs: Map<String, AttrVal>? = null) : Term

  data class Exists(val params: List<Representation.SortedInner>, val body: Term, override var attrs: Map<String, AttrVal>? = null) : Term

  data class Match(val term: Term, val cases: List<Representation.Case>, override var attrs: Map<String, AttrVal>? = null) : Term
}
