package org.yurusanp.musket.syntax

import java.util.*

/**
 * Literal node.
 */
sealed interface Lit : Node {
  data class Bit(val value: Boolean) : Lit {
    override fun dump(): String = value.toString()
  }

  data class Num(val value: Int) : Lit {
    override fun dump(): String = value.toString()
  }

  data class BitVec(val bs: BitSet, val sz: Int) : Lit {
    override fun dump(): String = (sz - 1 downTo 0)
      .map { pos -> if (bs.get(pos)) '1' else '0' }
      .joinToString("")
  }
}
