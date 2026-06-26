plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.shadow)
}

group = "io.github.alxiw"
version = "1.0"
description = "archiver"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.org.apache.ant.ant)
    implementation(libs.commons.cli.commons.cli)
}

kotlin {
    jvmToolchain(17)
}

tasks.named<Jar>("shadowJar") {
    archiveClassifier.set("") // to name archiver-1.0.jar instead of archiver-1.0-all.jar
    manifest {
        attributes["Main-Class"] = "io.github.alxiw.archiver.ArchiverKt"
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}
