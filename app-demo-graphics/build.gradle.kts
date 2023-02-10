plugins {
    id("application")
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":lib"))
}

application {
    mainClass.set("dev.patbeagan.app.demo.graphics.MainKt")
}