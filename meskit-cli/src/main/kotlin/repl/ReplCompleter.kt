package org.yurusanp.meskit.cli.repl

import org.jline.reader.Candidate
import org.jline.reader.Completer
import org.jline.reader.impl.completer.AggregateCompleter
import org.yurusanp.meskit.parser.SemGuSLexer

val literalNames: List<String> = (
  SemGuSLexer::class.java.getDeclaredField("_LITERAL_NAMES")
    .apply { isAccessible = true }
    .get(null) as Array<*>
  ).toList().mapNotNull { (it as String).removeSurrounding("'", "'") }

val reservedCandidates: List<Candidate> = literalNames.map(::Candidate)

val reservedCompleter: Completer = Completer { _, _, candidates ->
  candidates.addAll(reservedCandidates)
}

val replCompleter: Completer = AggregateCompleter(reservedCompleter)
