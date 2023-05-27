package org.yurusanp.meskit.parser

import io.kotest.core.spec.style.FunSpec
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.Token

class ParserTest : FunSpec(
  {
    test("Playground") {
      // unicode
      val input = "(define-fun |一个| () Int 1)"
      val charStream = CharStreams.fromString(input)
      val lexer = SemGuSLexer(charStream)
      val tokenStream = CommonTokenStream(lexer)
      tokenStream.fill()
      val tokensNoEOF: List<Token> = tokenStream.tokens.dropLast(1)

      println(tokensNoEOF.joinToString("\n") { it.toString() })
    }
  },
)
