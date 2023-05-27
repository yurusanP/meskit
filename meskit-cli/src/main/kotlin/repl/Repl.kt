package org.yurusanp.meskit.cli.repl

import org.jline.reader.*
import org.jline.reader.impl.DefaultHighlighter
import org.jline.reader.impl.history.DefaultHistory
import org.jline.terminal.Terminal
import org.jline.terminal.TerminalBuilder
import org.yurusanp.meskit.BaseInterpreter
import org.yurusanp.meskit.cli.util.interpret
import org.yurusanp.meskit.parser.SemGuSVisitor
import java.io.File

// TODO: implement return type as pretty doc
internal class Repl(private val interpreter: SemGuSVisitor<Unit> = BaseInterpreter()) {
  private val terminal: Terminal = TerminalBuilder.builder()
    .jansi(true)
    .build()

  private val history: History = DefaultHistory()

  private val parser: Parser = ReplParser()

  private val highlighter: Highlighter = DefaultHighlighter()

  private val completer: Completer = ReplCompleter()

  // TODO: allow customization
  private val historyFilePath: String = File(System.getProperty("user.home"))
    .resolve(".meskit/history").path

  private val reader: LineReader = LineReaderBuilder.builder()
    .terminal(terminal)
    .history(history)
    .parser(parser)
    .highlighter(highlighter)
    .completer(completer)
    .variable(LineReader.HISTORY_FILE, historyFilePath)
    .variable(LineReader.SECONDARY_PROMPT_PATTERN, "... ")
    .option(LineReader.Option.INSERT_TAB, true)
    .build()

  fun start() {
    while (true) {
      try {
        val line: String = reader.readLine(">>> ")
        line.interpret(interpreter)
      } catch (e: UserInterruptException) {
        // Ignore
      } catch (e: EndOfFileException) {
        return
      } catch (e: Exception) {
        // TODO: https://github.com/jline/jline3/issues/229
        terminal.writer().println("${e.javaClass.simpleName}: ${e.message}")
      }
    }
  }
}
