import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.6.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("org.jlleitschuh.gradle.ktlint") version "10.2.1"
}

repositories {
    mavenLocal()
    mavenCentral()
}

childProjects
    .onEach { println(it) }
    .filter { it.key in listOf("app", "lib") }
    .forEach { entry ->
        entry.value.run {
            group = "dev.patbeagan"
            version = "0.4.1"
            afterEvaluate {
                tasks {
                    named<ShadowJar>("shadowJar") {
                        minimize()
                        doLast {
                            outputs
                                .files
                                .files
                                .map { it.name to "${it.length() / 1_000_000}mb" }
                                .let { println(it) }
                        }
                    }
                }
            }
        }
    }
