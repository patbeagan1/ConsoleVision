plugins {
//    id("application")
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
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.mod4j.org.apache.commons:cli:1.0.0")
    implementation(project(":lib"))
    implementation(compose.desktop.currentOs)
    // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-coroutines-core
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
}

compose.desktop {
    application {
        mainClass = "MainKt"
    }
}
//
//application {
//    mainClass.set("dev.patbeagan.consolevision.MainKt")
//}
