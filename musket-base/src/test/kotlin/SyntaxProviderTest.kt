package org.meskit.musket

import io.kotest.core.spec.style.FunSpec
import org.yurusanp.meskit.parser.interpret
import org.yurusanp.musket.syntax.SyntaxProvider

const val dollar = '$'

class SyntaxProviderTest : FunSpec(
  {
    test("Translate term types E, B") {
      val input = """
      (declare-term-types
        ((E 0) (B 0))
        (
          (
            (${dollar}x)
            (${dollar}y)
            (${dollar}z)
            (${dollar}0)
            (${dollar}1)
            (${dollar}+)
            (${dollar}ite B E E)
          )
          (
            (${dollar}t)
            (${dollar}f)
            (${dollar}not B)
            (${dollar}and B B)
            (${dollar}or B B)
            (${dollar}< E E)
          )
        )
      )
      """.trimIndent()
      val syntaxProvider = SyntaxProvider()
      input.interpret(syntaxProvider)

      val inverses: MutableMap<String, String> = syntaxProvider.st.analyzer.st.symMan.inverses
      syntaxProvider.st.adTypeDefs.forEach { adTypeDef ->
        val dumped: String = adTypeDef.dump()
        val inversed: String = dumped.replace(Regex("""MES__\d+""")) { matchResult ->
          inverses[matchResult.value] ?: matchResult.value
        }
        println(dumped)
        println("// $inversed")
      }
    }
  },
)
