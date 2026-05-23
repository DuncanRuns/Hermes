pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.kikugie.dev/snapshots") { name = "KikuGie Snapshots" }
        maven("https://maven.ornithemc.net/snapshots")
        maven("https://maven.ornithemc.net/releases")
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.9.2"
}

stonecutter {
    create(rootProject) {
        versions(
            "1.14.4",
        )
        vcsVersion = "1.14.4"
    }
}

rootProject.name = "Hermes"