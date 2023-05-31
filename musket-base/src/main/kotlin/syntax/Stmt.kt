package org.yurusanp.musket.syntax

import org.yurusanp.musket.syntax.Node.*

/**
 * Statement node.
 */
sealed interface Stmt : Node {
  /**
   * Assert statement.
   */
  data class Assert(val expr: Expr) : Stmt {
    override fun dump(): String = "assert ${expr.dump()};"
  }

  /**
   * Return statement.
   */
  data class Return(val expr: Expr) : Stmt {
    override fun dump(): String = "return ${expr.dump()};"
  }

  /**
   * Generic effect statement.
   */
  data class Generic(val expr: Expr) : Stmt {
    override fun dump(): String = "${expr.dump()};"
  }

  /**
   * Variable declaration.
   */
  data class VarDec(val annSym: AnnSym) : Stmt {
    override fun dump(): String = "${annSym.dump()};"
  }

  /**
   * Variable definition.
   */
  data class VarDef(val annSym: AnnSym, val expr: Expr) : Stmt {
    override fun dump(): String = "${annSym.dump()} = ${expr.dump()};"
  }

  /**
   * Function definition.
   */
  data class FunDef(val annSym: AnnSym, val params: List<AnnSym>, val stmts: List<Stmt>) : Stmt {
    override fun dump(): String = let {
      val paramsDump = params.joinToString(", ", transform = AnnSym::dump)
      val stmtsDump = stmts.joinToString(" ", transform = Stmt::dump)
      "${annSym.dump()}($paramsDump) { $stmtsDump }"
    }
  }

  /**
   * Struct definition.
   */
  data class StructDef(val ctor: Ctor) : Stmt {
    override fun dump(): String = "struct ${ctor.dump()}"
  }

  /**
   * Algebraic data type definition.
   */
  data class ADTypeDef(val adt: ADType) : Stmt {
    override fun dump(): String = adt.dump()
  }
}
