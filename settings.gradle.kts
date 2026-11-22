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

dependencyResolutionManagement {
    repositories {
        maven("https://maven.kikugie.dev/snapshots")
    }

    versionCatalogs {
        create("ft") { from("dev.kikugie.fletching-table:fletching-table.catalog:0.2.0-alpha.14") }
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.10"
}

stonecutter {
    create(rootProject) {
        val supportedVersions = arrayOf(
            "26.1",
            "26.1.1",
            "26.1.2",
            "26.2",
            "26.3",
        )
        versions(*supportedVersions)
        vcsVersion = supportedVersions.last()
    }
}

rootProject.name = "Hermes"