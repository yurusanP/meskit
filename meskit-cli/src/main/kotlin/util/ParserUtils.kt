package org.yurusanp.meskit.cli.util

import org.antlr.v4.runtime.*
import org.yurusanp.meskit.parser.SemGuSLexer
import org.yurusanp.meskit.parser.SemGuSParser
import org.yurusanp.meskit.parser.SemGuSVisitor

internal fun String.tokenize(): List<Token> = let { line ->
  val charStream: CodePointCharStream = CharStreams.fromString(line)
  val lexer = SemGuSLexer(charStream).apply {
    removeErrorListeners()
    addErrorListener(ThrowingErrorListener)
  }
  val tokenStream: CommonTokenStream = CommonTokenStream(lexer).apply { fill() }
  tokenStream.tokens.dropLast(1)
}

internal fun <R> String.interpret(interpreter: SemGuSVisitor<R>): R = let {
  val charStream: CodePointCharStream = CharStreams.fromString(this)
  val lexer = SemGuSLexer(charStream).apply {
    removeErrorListeners()
    addErrorListener(ThrowingErrorListener)
  }
  val tokenStream: CommonTokenStream = CommonTokenStream(lexer)
  val parser = SemGuSParser(tokenStream).apply {
    removeErrorListeners()
    addErrorListener(ThrowingErrorListener)
  }
  interpreter.visit(parser.start())
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
