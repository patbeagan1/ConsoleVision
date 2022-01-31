import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("application")
    id("com.github.johnrengelman.shadow")
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("ch.qos.logback:logback-classic:1.2.5")
    implementation("commons-codec:commons-codec:1.13")
    implementation("io.ktor:ktor-server-core:1.6.7")
    implementation("io.ktor:ktor-server-netty:1.6.7")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    implementation("org.mod4j.org.apache.commons:cli:1.0.0")
    implementation(project(":lib"))
}

application {
    mainClass.set("dev.patbeagan.consolevision.MainKt")
}

group = "dev.patbeagan"
version = "0.1.1"

tasks {
    named<ShadowJar>("shadowJar") {
        minimize()
    }
}
