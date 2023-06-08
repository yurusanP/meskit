package org.yurusanp.meskit.cli.repl

import org.antlr.v4.runtime.Token
import org.jline.reader.CompletingParsedLine
import org.jline.reader.EOFError
import org.jline.reader.ParsedLine
import org.jline.reader.Parser
import org.jline.reader.Parser.ParseContext
import org.yurusanp.meskit.parser.tokenize

internal class ReplParser : Parser {
  override fun parse(line: String, cursor: Int, context: ParseContext): ParsedLine {
    if (line.isBlank()) return ReplParsedLine(
      wordCursor = 0,
      wordIndex = 0,
      words = emptyList(),
      line = line,
      cursor = cursor,
    )

    val tokens: List<Token> = line.tokenize()
    if (context != ParseContext.COMPLETE && context != ParseContext.SPLIT_LINE) {
      if (!tokens.matchBrackets()) {
        throw EOFError(-1, -1, "Multiline")
      }
    }

    val wordTokens: List<Token> = tokens.filter {
      it.text != "(" && it.text != ")"
    }

    // Example of a token with startIndex = 0 and stopIndex = 2
    // index    0 1 2
    // input   |m|e|s|
    // cursor  0 1 2 3
    val ix: Int = wordTokens.lowerBound { token ->
      // the convention in DefaultParser is selecting the next word if the cursor is not pointing at a word
      cursor in token.startIndex..token.stopIndex + 1
        || token.startIndex > cursor
    }

    val wordCursor =
      if (ix == wordTokens.size || wordTokens[ix].startIndex > cursor) 0
      else cursor - wordTokens[ix].startIndex

    return ReplParsedLine(
      wordCursor,
      wordIndex = ix,
      words = wordTokens.map(Token::text),
      line = line,
      cursor = cursor,
    )
  }

  private data class ReplParsedLine(
    private val wordCursor: Int,
    private val wordIndex: Int,
    private val words: List<String>,
    private val line: String,
    private val cursor: Int,
  ) : CompletingParsedLine {
    override fun word(): String = if (wordIndex != words.size) words[wordIndex] else ""
    override fun wordCursor(): Int = wordCursor
    override fun wordIndex(): Int = wordIndex
    override fun words(): List<String> = words
    override fun line(): String = line
    override fun cursor(): Int = cursor
    override fun escape(candidate: CharSequence, complete: Boolean): CharSequence = candidate
    override fun rawWordCursor(): Int = wordCursor
    override fun rawWordLength(): Int = word().length
  }
}

private fun <T> List<T>.lowerBound(fromIndex: Int = 0, toIndex: Int = size, predicate: (T) -> Boolean): Int = let {
  var lo = fromIndex
  var hi = toIndex
  while (lo < hi) {
    val mid = (lo + hi).ushr(1)
    if (!predicate((get(mid)))) lo = mid + 1
    else hi = mid
  }
  lo
}

private fun List<Token>.matchBrackets(): Boolean {
  var parOpenCnt = 0
  for (token in this) {
    when (token.text) {
      "(" -> ++parOpenCnt
      ")" -> {
        if (parOpenCnt == 0) return false
        --parOpenCnt
      }
    }
  }
  return parOpenCnt == 0
}
