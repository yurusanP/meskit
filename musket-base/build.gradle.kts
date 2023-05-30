plugins {
  alias(libs.plugins.serialization)
}

dependencies {
  api(project(":meskit-base"))
  implementation(libs.kotlinx.collections.immutable)
  implementation(libs.kotlinx.serialization.json)
}
