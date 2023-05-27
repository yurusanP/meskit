package org.yurusanp.meskit.cli.util

import org.antlr.v4.runtime.*
import org.yurusanp.meskit.parser.SemGuSLexer

fun String.tokenize(): List<Token> = let { line ->
  val charStream: CodePointCharStream = CharStreams.fromString(line)
  val lexer = SemGuSLexer(charStream).apply {
    removeErrorListeners()
    addErrorListener(ThrowingErrorListener)
  }
  val tokenStream: CommonTokenStream = CommonTokenStream(lexer).apply { fill() }
  tokenStream.tokens.dropLast(1)
}

object ThrowingErrorListener : BaseErrorListener() {
  override fun syntaxError(
    recognizer: Recognizer<*, *>,
    offendingSymbol: Any?,
    line: Int,
    charPositionInLine: Int,
    msg: String,
    e: RecognitionException,
  ) {
    throw SyntaxErrorException("line $line:$charPositionInLine $msg")
  }
}

class SyntaxErrorException(msg: String) : IllegalStateException(msg)
