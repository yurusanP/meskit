plugins {
  alias(libs.plugins.serialization)
}

dependencies {
  api(project(":meskit-parser"))
}
