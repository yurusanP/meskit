plugins {
  application
}

dependencies {
  implementation(libs.bundles.jline)
  implementation(libs.clikt)
  implementation(project(":meskit-base"))
  implementation(project(":musket-base"))
}

application {
  mainClass.set("org.yurusanp.meskit.cli.MainKt")
}
