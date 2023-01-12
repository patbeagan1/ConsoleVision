import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    kotlin("jvm") version "1.8.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("org.jlleitschuh.gradle.ktlint") version "10.2.1"
}

repositories {
    mavenLocal()
    mavenCentral()
}

version = "0.8.0"
group = "dev.patbeagan"

childProjects
    .onEach { println(it) }
    .filter { it.key in listOf("app", "lib", "server") }
    .forEach { entry ->
        entry.value.let { project ->
            project.group = group
            project.version = version
            project.afterEvaluate {
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
