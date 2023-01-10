plugins {
    id("application")
    id("com.github.johnrengelman.shadow")
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.mod4j.org.apache.commons:cli:1.0.0")
    implementation(project(":lib"))
}

application {
    mainClass.set("dev.patbeagan.consolevision.MainKt")
}
