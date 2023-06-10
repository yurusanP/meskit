package org.yurusanp.meskit.resolver

import io.kotest.core.spec.style.FunSpec
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.yurusanp.meskit.parser.solve
import org.yurusanp.meskit.resolve.Resolver
import org.yurusanp.meskit.resolve.ResolverResult
import org.yurusanp.meskit.surface.Representation
import org.yurusanp.meskit.symtab.inverse

const val dollar = '$'

val format = Json {
  prettyPrint = true
}

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

      val serialized: String = format.encodeToString((res as ResolverResult.Multiple<Representation.TermTypeDef>).reps)
      val inversed = serialized.inverse(resolver.st.symMan)
//      println(serialized)
      println(inversed)

//      val deserialized: List<Representation.TermTypeDef> = format.decodeFromString(
//        ListSerializer(Representation.TermTypeDef.serializer()),
//        serialized,
//      )
//      println(deserialized)
    }
  },
)
