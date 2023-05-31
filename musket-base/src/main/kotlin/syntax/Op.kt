package org.yurusanp.musket.syntax

/**
 * Operator node.
 */
sealed interface Op : Node {
  val value: String

  override fun dump(): String = value

  enum class Unary(override val value: String) : Op {
    NOT("!"),
    NEG("-"),
  }

  enum class Binary(override val value: String) : Op {
    AND("&&"),
    OR("||"),
    XOR("^"),
    EQ("=="),

    PLUS("+"),
    MINUS("-"),
    TIMES("*"),
    DIV("/"),
    MOD("%"),

    LTE("<="),
    LT("<"),
    GTE(">="),
    GT(">"),

    BVAND("&"),
    BVOR("|"),
    SHL("<<"),
    SHR(">>"),
  }
}
