plugins {
    id("application")
    id("com.github.johnrengelman.shadow")
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

object Versions {
    const val versionKtor = "1.6.7"
}

dependencies {
    implementation("ch.qos.logback:logback-classic:1.2.5")
    implementation("commons-codec:commons-codec:1.13")
    implementation("io.insert-koin:koin-core:3.1.2")
    implementation("io.ktor:ktor-html-builder:${Versions.versionKtor}")
    implementation("io.ktor:ktor-server-core:${Versions.versionKtor}")
    implementation("io.ktor:ktor-server-netty:${Versions.versionKtor}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    implementation("org.mod4j.org.apache.commons:cli:1.0.0")
    implementation(project(":lib"))
}

application {
    mainClass.set("dev.patbeagan.consolevision.MainKt")
}
