import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    id("application")
    kotlin("jvm") version "1.8.22"

    id("org.javamodularity.moduleplugin") version "1.8.12"

    id("org.openjfx.javafxplugin") version "0.0.13"
    id("org.beryx.jlink") version "2.25.0"
}

group "lofitsky"
version "1.0"

repositories {
    mavenCentral()
    mavenLocal()
}

java.sourceCompatibility = JavaVersion.VERSION_17

kotlin {
    jvmToolchain(17)
}

javafx {
    version = "17.0.6"
    modules = listOf("javafx.controls", "javafx.fxml")
}

dependencies {
    implementation("org.controlsfx:controlsfx:11.2.1")
    implementation("com.dlsc.formsfx:formsfx-core:11.6.0") {
        exclude(group = "org.openjfx")
    }
    implementation("org.kordamp.bootstrapfx:bootstrapfx-core:0.4.0")

    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    implementation("lofitsky:komptube-common:1.0")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

application {
    mainModule = "lofitsky.komptubeui"
    mainClass = "lofitsky.komptube.local.ui.KomptubeUIApplication"
}
