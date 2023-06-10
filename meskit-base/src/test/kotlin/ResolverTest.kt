package org.yurusanp.meskit.resolver

import io.kotest.core.spec.style.FunSpec
import org.yurusanp.meskit.parser.solve
import org.yurusanp.meskit.resolve.Resolver
import org.yurusanp.meskit.resolve.ResolverResult

const val dollar = '$'

class ResolverTest : FunSpec(
  {
    test("Resolve term types E, B") {
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
      val resolver = Resolver()
      val res: ResolverResult = input.solve(resolver)
      println(res)
    }
  },
)
