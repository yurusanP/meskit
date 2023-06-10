package org.yurusanp.meskit.surface

sealed interface Index : Representation {
  // NOTE: not appearing in SMT-LIB defined theories
  // in a user-defined theory, you could have something like (_ move up)
  data class Inner(val inner: String) : Index

  data class Num(val num: Int) : Index
}
