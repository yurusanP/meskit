package org.yurusanp.meskit.cli.repl

import org.jline.reader.Candidate
import org.jline.reader.Completer
import org.jline.reader.impl.completer.AggregateCompleter
import org.yurusanp.meskit.parser.SemGuSParser

// TODO: builtin theory and user-defined symbols
internal class ReplCompleter : Completer by AggregateCompleter(
  parOpenCompleter,
  cmdCompleter,
  argCompleter
)

private val parOpenCompleter: Completer = Completer { _, parsed, candidates ->
  if (parsed.line().isBlank()) {
    candidates.add(Candidate("(", "(", null, null, null, null, false))
  }
}

private val literalNames: Array<*> =
  SemGuSParser::class.java.getDeclaredField("_LITERAL_NAMES")
    .apply { isAccessible = true }
    .get(null) as Array<*>

private val symbolNames: Array<*> =
  SemGuSParser::class.java.getDeclaredField("_SYMBOLIC_NAMES")
    .apply { isAccessible = true }
    .get(null) as Array<*>

private val vocabulary: List<Pair<String, String>> = literalNames
  .asSequence()
  .filterNotNull()
  .map { (it as String).removeSurrounding("'", "'") }
  .zip(symbolNames.asSequence().filterNotNull().map { it as String })
  .toList()

private val cmdCandidates: List<Candidate> = vocabulary
  .asSequence()
  .filter { it.second.startsWith("CMD_") }
  .map {
    Candidate(it.first, it.first, null, null, null, null, false)
  }.toList()

private val cmdCompleter: Completer = Completer { _, parsed, candidates ->
  if (parsed.line().isBlank()) return@Completer
  if (parsed.line().trim().first() == '(' && parsed.wordIndex() == 0) {
    candidates.addAll(cmdCandidates)
  }
}

private fun dummy(vararg desc: String): List<Candidate> = desc
  .map { Candidate("", "", null, it, null, null, false) }

private val argMap: Map<String, List<Candidate>> = mapOf(
  "assume" to dummy("assume term"),
  "check-synth" to dummy("check-synth"),
  "constraint" to dummy("constraint term"),
  "declare-datatype" to dummy("declare-datatype sym [ctor ...]"),
  "declare-datatypes" to dummy(
    "declare-datatypes [sym ...] [ctors ...]",
    "declare-datatypes [sym ...] [par_ctors ...]"
  ),
  // TODO: but this is not going to play well...
)

private val argCompleter: Completer = Completer { _, parsed, candidates ->
  if (parsed.wordIndex() > 0) {
    val argCandidate: List<Candidate> = argMap[parsed.words()[0]] ?: return@Completer
    candidates.addAll(argCandidate)
  }
}
