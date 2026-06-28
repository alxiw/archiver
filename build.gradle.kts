plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.shadow)
}

group = "io.github.alxiw"
version = "1.0"
description = "archiver"
java.sourceCompatibility = JavaVersion.VERSION_21

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.org.apache.commons.compress)
    implementation(libs.ajalt.clikt)
}

kotlin {
    jvmToolchain(21)
}

tasks.named<Jar>("shadowJar") {
    archiveClassifier.set("") // to name archiver-1.0.jar instead of archiver-1.0-all.jar
    manifest {
        attributes["Main-Class"] = "io.github.alxiw.archiver.AppKt"
    }
}
