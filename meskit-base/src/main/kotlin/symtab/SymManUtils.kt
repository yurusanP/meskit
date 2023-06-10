package org.yurusanp.meskit.symtab

fun String.inverse(symMan: SymMan): String = replace(Regex("""MES__\d+""")) { matchResult ->
  symMan.inverses[matchResult.value] ?: matchResult.value
}
