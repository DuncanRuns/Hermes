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

val developSingleVersion = null

stonecutter {
    create(rootProject) {
        // See https://stonecutter.kikugie.dev/wiki/start/#choosing-minecraft-versions
        if(developSingleVersion == null) {
            versions("1.14.3", "1.14.4", "1.15.2", "1.16.1", "1.16.5", "1.17.1", "1.18.1", "1.18.2", "1.19.2")
            vcsVersion = "1.16.1"
        } else {
            version(developSingleVersion)
        }
    }
}

rootProject.name = "Hermes"