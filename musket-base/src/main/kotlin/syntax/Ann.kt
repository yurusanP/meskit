package org.yurusanp.musket.syntax

/**
 * Type annotation node.
 */
sealed interface Ann : Node {
  data object Void : Ann {
    override fun dump(): String = "void"
  }

  data object Fun : Ann {
    override fun dump(): String = "fun"
  }

  data object Bit : Ann {
    override fun dump(): String = "bit"
  }

  data object Num : Ann {
    override fun dump(): String = "int"
  }

  data class BitVec(val sz: Int) : Ann {
    override fun dump(): String = "bit[$sz]"
  }

  data class DType(val sym: String) : Ann {
    override fun dump(): String = sym
  }
}
