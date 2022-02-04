import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
    id("com.github.johnrengelman.shadow")
    id("org.jlleitschuh.gradle.ktlint")
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.6.10")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.3")
    implementation("org.mod4j.org.apache.commons:cli:1.0.0")
    testImplementation("junit:junit:4.13.1")
    testImplementation("org.jetbrains.kotlin:kotlin-reflect:1.3.72")
}


group = "dev.patbeagan"
version = "0.4.0"
tasks {
    named<ShadowJar>("shadowJar") {
        minimize()
    }
}


