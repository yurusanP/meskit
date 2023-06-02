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
  data class VarDec(val annedSym: AnnedSym) : Stmt {
    override fun dump(): String = "${annedSym.dump()};"
  }

  /**
   * Variable definition.
   */
  data class VarDef(val annedSym: AnnedSym, val expr: Expr) : Stmt {
    override fun dump(): String = "${annedSym.dump()} = ${expr.dump()};"
  }

  /**
   * Function definition.
   */
  data class FunDef(val annedSym: AnnedSym, val params: List<AnnedSym>, val stmts: List<Stmt>) : Stmt {
    override fun dump(): String = let {
      val paramsDump = params.joinToString(", ", transform = AnnedSym::dump)
      val stmtsDump = stmts.joinToString(" ", transform = Stmt::dump)
      "${annedSym.dump()}($paramsDump) { $stmtsDump }"
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
