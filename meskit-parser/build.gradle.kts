plugins {
  antlr
}

dependencies {
  antlr(libs.antlr4)
  runtimeOnly(libs.antlr4.runtime)
}

configurations {
  api {
    // exclude Antlr v3 runtime from the classpath
    exclude(group = "org.antlr", module = "antlr-runtime")
  }
}

tasks.generateGrammarSource.configure {
  outputDirectory = buildDir.resolve("generated-src/antlr/main/org/yurusanp/meskit/parser")
  arguments.addAll(listOf("-package", "org.yurusanp.meskit.parser"))
  arguments.add("-no-listener")
  arguments.add("-visitor")
}

tasks.generateGrammarSource {
  doLast {
    delete(projectDir.resolve("src/main/gen"))
  }
}
