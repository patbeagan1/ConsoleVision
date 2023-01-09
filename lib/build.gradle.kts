plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
    id("com.github.johnrengelman.shadow")
    id("org.jlleitschuh.gradle.ktlint")
    `maven-publish`
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.6.10")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.mod4j.org.apache.commons:cli:1.0.0")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlin:kotlin-reflect:1.3.72")
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