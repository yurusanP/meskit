package org.yurusanp.meskit.parser

import org.antlr.v4.runtime.*

fun String.tokenize(): List<Token> = let { line ->
  val charStream: CodePointCharStream = CharStreams.fromString(line)
  val lexer = SemGuSLexer(charStream).apply {
    // doing completion shouldn't throw exceptions
    removeErrorListeners()
  }
  val tokenStream: CommonTokenStream = CommonTokenStream(lexer).apply { fill() }
  tokenStream.tokens.dropLast(1)
}

fun <R> String.solve(solver: SemGuSVisitor<R>): R = let {
  val charStream: CodePointCharStream = CharStreams.fromString(this)
  val lexer = SemGuSLexer(charStream).apply {
    removeErrorListeners()
    addErrorListener(ThrowingErrorListener)
  }
  val tokenStream = CommonTokenStream(lexer)
  val parser = SemGuSParser(tokenStream).apply {
    removeErrorListeners()
    addErrorListener(ThrowingErrorListener)
  }
  solver.visit(parser.start())
}

internal object ThrowingErrorListener : BaseErrorListener() {
  override fun syntaxError(
    recognizer: Recognizer<*, *>,
    offendingSymbol: Any?,
    line: Int,
    charPositionInLine: Int,
    msg: String,
    e: RecognitionException?,
  ) {
    throw SyntaxErrorException("line $line:$charPositionInLine $msg")
  }
}

internal class SyntaxErrorException(msg: String) : IllegalStateException(msg)
