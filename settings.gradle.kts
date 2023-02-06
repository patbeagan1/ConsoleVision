rootProject.name = "console-vision"

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

include(":app")
include(":lib")
include(":server")
include(":consolevision-base")
include(":consolevision-style")
include(":consolevision-compose")