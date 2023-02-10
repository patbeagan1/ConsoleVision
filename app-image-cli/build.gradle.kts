plugins {
    id("application")
    id("com.github.johnrengelman.shadow")
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.mod4j.org.apache.commons:cli:1.0.0")
    implementation(project(":consolevision-jvm"))
}

application {
    mainClass.set("dev.patbeagan.app.image.cli.MainKt")
}