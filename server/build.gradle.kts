plugins {
    id("application")
    id("com.github.johnrengelman.shadow")
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("ch.qos.logback:logback-classic:1.4.5")
    implementation("commons-codec:commons-codec:1.15")
    implementation("io.insert-koin:koin-core:3.3.2")
    implementation("io.ktor:ktor-server-html-builder:2.2.1")
    implementation("io.ktor:ktor-server-core:2.2.1")
    implementation("io.ktor:ktor-server-netty:2.2.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.mod4j.org.apache.commons:cli:1.0.0")
    implementation(project(":lib"))
}

application {
    mainClass.set("dev.patbeagan.consolevision.MainKt")
}
