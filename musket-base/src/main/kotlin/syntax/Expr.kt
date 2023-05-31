package org.yurusanp.musket.syntax

/**
 * Expression node.
 */
sealed interface Expr : Node {
  /**
   * Literal expression.
   */
  data class Literal(val lit: Lit) : Expr {
    override fun dump(): String = lit.dump()
  }

  /**
   * Symbol reference expression.
   */
  data class Ref(val sym: String, val namespace: Namespace? = null) : Expr {
    override fun dump(): String =
      namespace?.let { "${it.dump()}$sym" } ?: sym
  }

  /**
   * Call expression.
   */
  data class Call(val ref: Ref, val args: List<Expr>) : Expr {
    override fun dump(): String = let {
      val argsDump = args.joinToString(", ", transform = Expr::dump)
      "${ref.dump()}($argsDump)"
    }
  }

  /**
   * Assignment expression.
   */
  data class Assign(val ref: Ref, val expr: Expr) : Expr {
    override fun dump(): String = "${ref.dump()} = ${expr.dump()}"
  }

  /**
   * Instantiation expression.
   */
  data class Instantiate(val ref: Ref, val assigns: List<Assign>, val isTmp: Boolean = false) : Expr {
    override fun dump(): String = let {
      val assignsDump = assigns.joinToString(", ", transform = Assign::dump)
      if (isTmp) "|${ref.dump()}|($assignsDump)" else "new ${ref.dump()}($assignsDump)"
    }
  }

  /**
   * If-then-else expression.
   */
  data class Ite(val cond: Expr, val then: Expr, val els: Expr) : Expr {
    override fun dump(): String = "(${cond.dump()} ? ${then.dump()} : ${els.dump()})"
  }

  /**
   * Unary expression.
   */
  data class Unary(val op: Op.Unary, val expr: Expr) : Expr {
    override fun dump(): String = "$op${expr.dump()}"
  }

  /**
   * Binary expression.
   */
  data class Binary(val op: Op.Binary, val lhs: Expr, val rhs: Expr) : Expr {
    override fun dump(): String = "(${lhs.dump()} $op ${rhs.dump()})"
  }

  /**
   * Nary expression.
   */
  data class Nary(val op: Op.Binary, val exprs: List<Expr>) : Expr {
    override fun dump(): String = let {
      val exprsDump = exprs.joinToString(" $op ", transform = Expr::dump)
      "($exprsDump)"
    }
  }

  /**
   * Arrow function expression.
   */
  data class Arrow(val params: List<String>, val expr: Expr) : Expr {
    override fun dump(): String = let {
      val paramsDump = params.joinToString(", ")
      "($paramsDump) -> ${expr.dump()}"
    }
  }

  /**
   * Non-deterministic hole expression.
   */
  data class Hole(val bits: Int) : Expr {
    override fun dump(): String = "??($bits)"
  }

  /**
   * Non-deterministic choice expression.
   */
  data class Choice(val exprs: List<Expr>) : Expr {
    override fun dump(): String = let {
      val exprsDump = exprs.joinToString(" | ", transform = Expr::dump)
      "{| $exprsDump |}"
    }
  }
}
