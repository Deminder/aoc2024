import org.gradle.internal.declarativedsl.parsing.main

plugins {
    application
    kotlin("jvm") version "2.0.21"
}

group = "com.github.deminder"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-cli-jvm:0.3.5")
}


application {
    mainClass = "com.github.deminder.MainKt"
}


tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}