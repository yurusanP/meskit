package org.yurusanp.meskit.surface

import kotlinx.serialization.Serializable

@Serializable
sealed interface Term : Representation {
  var attrs: Map<String, AttrVal>?

  @Serializable
  data class Literal(val specConst: SpecConst, override var attrs: Map<String, AttrVal>? = null) : Term

  // TODO: deal with qual later
  @Serializable
  data class Ref(val ident: Ident, override var attrs: Map<String, AttrVal>? = null) : Term

  @Serializable
  data class App(val ident: Ident, val args: List<Term>, override var attrs: Map<String, AttrVal>? = null) : Term

  @Serializable
  data class Let(val bindings: List<Representation.Binding>, val body: Term, override var attrs: Map<String, AttrVal>? = null) : Term

  @Serializable
  data class Forall(val params: List<Representation.SortedInner>, val body: Term, override var attrs: Map<String, AttrVal>? = null) : Term

  @Serializable
  data class Exists(val params: List<Representation.SortedInner>, val body: Term, override var attrs: Map<String, AttrVal>? = null) : Term

  @Serializable
  data class Match(val term: Term, val cases: List<Representation.Case>, override var attrs: Map<String, AttrVal>? = null) : Term
}
