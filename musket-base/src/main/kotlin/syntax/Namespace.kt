package org.yurusanp.musket.syntax

/**
 * Namespace node.
 */
sealed interface Namespace : Node {
  data class Instance(val sym: String) : Ann {
    override fun dump(): String = "$sym."
  }

  data class Package(val sym: String) : Ann {
    override fun dump(): String = "$sym@"
  }
}
