plugins {
    id("com.github.johnrengelman.shadow")
    kotlin("jvm")
    id("org.jetbrains.compose") version "1.2.2"
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    implementation(project(":lib"))
    implementation(project(":consolevision-compose"))
    implementation(compose.desktop.currentOs)
    // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-coroutines-core
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
}

compose.desktop {
    application {
        mainClass = "dev.patbeagan.app.demo.compose.MainKt"
    }
}
