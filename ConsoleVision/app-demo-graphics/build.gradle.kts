plugins {
    id("application")
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":consolevision-jvm"))
}

application {
    mainClass.set("dev.patbeagan.app.demo.graphics.MainKt")
}