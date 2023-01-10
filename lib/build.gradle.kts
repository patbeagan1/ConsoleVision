plugins {
    kotlin("jvm")
    id("java-library")
    id("com.github.johnrengelman.shadow")
    id("org.jlleitschuh.gradle.ktlint")
    `maven-publish`
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    api(project(":consolevision"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.mod4j.org.apache.commons:cli:1.0.0")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlin:kotlin-reflect:1.7.22")
}

publishing {
    repositories {
        maven {
            name = "ConsoleVision"
            url = uri("https://maven.pkg.github.com/patbeagan1/ConsoleVision")
            credentials {
                username = System.getenv("GITHUB_ACTOR_CONSOLE_VISION")
                password = System.getenv("GITHUB_TOKEN_CONSOLE_VISION")
            }
        }
    }
    publications {
        register<MavenPublication>("gpr") {
            from(components["java"])
            this.artifactId = "console-vision"
        }
    }
}
