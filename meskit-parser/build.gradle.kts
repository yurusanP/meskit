plugins {
  antlr
}

dependencies {
  antlr(libs.antlr4)
  runtimeOnly(libs.antlr4.runtime)
}

configurations {
  api {
    // exclude Antlr v3 runtime from the compile classpath
    exclude(group = "org.antlr", module = "antlr-runtime")
  }
  runtimeClasspath {
    // exclude Antlr v4 generator from the runtime classpath
    exclude(group = "org.antlr", module = "antlr4")
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
    fileTree(projectDir.resolve("src/main/antlr")).visit {
      if (!this.name.endsWith("g4")) {
        delete(this.file)
      }
    }
  }
}
