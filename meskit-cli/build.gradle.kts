plugins {
  application
}

dependencies {
  implementation(libs.bundles.jline)
  implementation(libs.clikt)
  implementation(project(":meskit-parser"))
}

application {
  mainClass.set("org.yurusanp.meskit.cli.MainKt")
}
