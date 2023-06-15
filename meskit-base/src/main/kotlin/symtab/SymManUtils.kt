package org.yurusanp.meskit.symtab

import kotlinx.serialization.Serializable

/**
 * A debugging function that retrieves user-defined surface symbols.
 */
fun String.inverse(symMan: SymMan): String = replace(Regex("""MES__\d+""")) { matchResult ->
  symMan.inverses[Inner(matchResult.value)] ?: matchResult.value
}

// TODO: inspect the serialization result
@JvmInline
@Serializable
value class Inner(val value: String) {
  override fun toString(): String = value
}
