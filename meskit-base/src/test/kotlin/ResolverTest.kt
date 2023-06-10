package org.yurusanp.meskit.resolver

import io.kotest.core.spec.style.FunSpec
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.yurusanp.meskit.parser.solve
import org.yurusanp.meskit.resolve.Resolver
import org.yurusanp.meskit.resolve.ResolverResult.*
import org.yurusanp.meskit.surface.Representation
import org.yurusanp.meskit.symtab.inverse

const val dollar = '$'

val format = Json {
  prettyPrint = true
}

class ResolverTest : FunSpec(
  {
    test("Resolve term type declaration for E, B") {
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
            (${dollar}+ E E)
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
      val reps: List<Representation> = (input.solve(resolver) as Multiple<Representation>).reps

      val serialized: String = format.encodeToString(reps)
      val inversed = serialized.inverse(resolver.st.symMan)
      println(inversed)
    }

    test("Resolve function definition for incr") {
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
            (${dollar}+ E E)
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
      (define-fun incr ((x E)) E
        (${dollar}+ x ${dollar}1)
      )
      """.trimIndent()

      val resolver = Resolver()
      val reps: List<Representation> = (input.solve(resolver) as Multiple<Representation>).reps

      val serialized: String = format.encodeToString(reps)
      val inversed = serialized.inverse(resolver.st.symMan)
      println(inversed)
    }
  },
)
