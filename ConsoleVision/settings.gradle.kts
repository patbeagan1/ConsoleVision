rootProject.name = "console-vision"

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven(url = "https://plugins.gradle.org/m2/")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}
include(":app-demo-compose")
include(":app-demo-graphics")
include(":app-image-cli")
include(":app-image-server")
include(":consolevision-jvm")
include(":consolevision-base")
include(":consolevision-style")
include(":consolevision-compose")
