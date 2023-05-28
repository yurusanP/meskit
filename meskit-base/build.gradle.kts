plugins {
  alias(libs.plugins.serialization)
}

dependencies {
  api(project(":meskit-parser"))
  implementation(libs.kotlinx.collections.immutable)
  implementation(libs.kotlinx.serialization.json)
}
