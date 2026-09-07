pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.kikugie.dev/releases") { name = "KikuGie Releases" }
        maven("https://maven.kikugie.dev/snapshots") { name = "KikuGie Snapshots" }
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.9.2"
}

stonecutter {
    create(rootProject) {
        val supportedVersions = arrayOf(
            "26.1",
            "26.1.1",
            "26.1.2",
            "26.2",
            "26.3-pre-2",
        )
        versions(*supportedVersions)
        vcsVersion = supportedVersions.last()
    }
}

rootProject.name = "Hermes"