package org.yurusanp.meskit.cli.repl

import org.jline.reader.Candidate
import org.jline.reader.Completer
import org.jline.reader.impl.completer.AggregateCompleter
import org.yurusanp.meskit.parser.SemGuSLexer

// TODO: builtin theory and user-defined symbols
internal class ReplCompleter : Completer by AggregateCompleter(reservedCompleter)

private val literalNames: List<String> = (
  SemGuSLexer::class.java.getDeclaredField("_LITERAL_NAMES")
    .apply { isAccessible = true }
    .get(null) as Array<*>
  ).toList().mapNotNull { (it as String).removeSurrounding("'", "'") }

private val reservedCandidates: List<Candidate> = literalNames.map(::Candidate)

private val reservedCompleter: Completer = Completer { _, _, candidates ->
  candidates.addAll(reservedCandidates)
}
