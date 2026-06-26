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
    id("dev.kikugie.stonecutter") version "0.9.6"
}

stonecutter {
    create(rootProject) {
        versions(
            "1.8",
            "1.8.1",
            "1.8.2",
            "1.8.3",
            "1.8.4",
            "1.8.5",
            "1.8.6",
            "1.8.7",
            "1.8.8",
            "1.8.9",
            "1.9",
            "1.9.1",
            "1.9.2",
            "1.9.3",
            "1.9.4",
            "1.10",
            "1.10.1",
            "1.10.2",
            "1.11",
            "1.11.1",
            "1.11.2",
            "1.12",
            "1.12.1",
            "1.12.2",
            "1.13",
            "1.13.1",
            "1.13.2",
        )
        vcsVersion = "1.13.2"
    }
}

rootProject.name = "Hermes"