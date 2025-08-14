plugins {
    kotlin("jvm")
    id("java-library")
    id("org.jetbrains.compose") version "1.2.2"
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(project(":consolevision-jvm"))
    implementation(compose.desktop.currentOs)
}
