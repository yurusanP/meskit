package org.yurusanp.musket.syntax

/**
 * AST node for the Sketch language.
 */
sealed interface Node {
  /**
   * Dump the AST node as a string. Only the top-level has line breaks.
   * NOTE: Kotlin string interpolation is efficient
   *
   * Use doc to pretty print instead.
   * TODO: migrate semgus-pretty to meskit
   */
  fun dump(): String

  /**
   * Top-level program node.
   */
  data class TopLevel(val stmts: List<Stmt>) : Node {
    override fun dump(): String =
      stmts.joinToString("\n", transform = Stmt::dump)
  }

  /**
   * Annotated symbol node.
   */
  data class AnnSym(val ann: Ann, val sym: String) : Node {
    override fun dump(): String = "${ann.dump()} $sym"
  }

  /**
   * Data constructor node.
   */
  data class Ctor(val sym: String, val fields: List<Stmt.VarDec>) : Node {
    override fun dump(): String = let {
      val fieldsDump = fields.joinToString(" ", transform = Stmt.VarDec::dump)
      "$sym { $fieldsDump }"
    }
  }

  /**
   * Algebraic data type node.
   */
  data class ADType(val sym: String, val ctors: List<Ctor>?, val subtypes: List<ADType>?) : Node {
    override fun dump(): String = let {
      val ctorsDump = ctors?.joinToString(" ", transform = Ctor::dump) ?: ""
      val subtypesDump = subtypes?.joinToString(" ", transform = ADType::dump) ?: ""
      val combined = listOf(ctorsDump, subtypesDump).joinToString(" ")
      "adt $sym { $combined }"
    }
  }
}
