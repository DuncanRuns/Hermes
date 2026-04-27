pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.kikugie.dev/snapshots") { name = "KikuGie Snapshots" }
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.9.2"
}

stonecutter {
    create(rootProject) {
        // See https://stonecutter.kikugie.dev/wiki/start/#choosing-minecraft-versions
        versions("1.14.4", "1.15.2", "1.16.1", "1.16.5")
        vcsVersion = "1.16.1"
    }
}

rootProject.name = "Hermes"