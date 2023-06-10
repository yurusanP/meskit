package org.yurusanp.meskit.musket.solver

import io.kotest.core.spec.style.FunSpec
import org.yurusanp.meskit.parser.solve
import org.yurusanp.musket.solve.Solver

const val dollar = '$'

class SolverTest : FunSpec(
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
      val solver = Solver()
      input.solve(solver)

      // TODO: extract a helper later
      val inverses: MutableMap<String, String> = solver.st.resolver.st.symMan.inverses
      solver.st.adTypeDefs.forEach { adTypeDef ->
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