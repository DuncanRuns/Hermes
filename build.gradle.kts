plugins {
    id("fabric-loom")
    kotlin("jvm") version "2.2.10"
    id("com.google.devtools.ksp") version "2.2.10-2.0.2"
    id("dev.kikugie.fletching-table.fabric") version "0.1.0-alpha.22"
}

version = "${property("mod.version")}+MC${stonecutter.current.version}"
base.archivesName = property("mod.id") as String

val minecraftJava = when {
    stonecutter.eval(stonecutter.current.version, ">=1.20.6") -> 21
    stonecutter.eval(stonecutter.current.version, ">=1.18") -> 17
    stonecutter.eval(stonecutter.current.version, ">=1.17") -> 16
    else -> 8
}

repositories {
    /**
     * Restricts dependency search of the given [groups] to the [maven URL][url],
     * improving the setup speed.
     */
    fun strictMaven(url: String, alias: String, vararg groups: String) = exclusiveContent {
        forRepository { maven(url) { name = alias } }
        filter { groups.forEach(::includeGroup) }
    }
    strictMaven("https://www.cursemaven.com", "CurseForge", "curse.maven")
    strictMaven("https://api.modrinth.com/maven", "Modrinth", "maven.modrinth")
    maven("https://jitpack.io") { name = "JitPack" }
}

dependencies {
    minecraft("com.mojang:minecraft:${stonecutter.current.version}")
    if (stonecutter.current.parsed <= "1.14.2") {
        mappings("net.fabricmc:yarn:${property("deps.yarn")}")
    } else {
        mappings("net.fabricmc:yarn:${property("deps.yarn")}:v2")
    }
    modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
    modCompileOnly("${property("deps.speedrunigt")}")

    modImplementation("${property("deps.hermes-core")}")
    include("${property("deps.hermes-core")}")
}

loom {
    fabricModJsonPath = rootProject.file("src/main/resources/fabric.mod.json") // Useful for interface injection
//    accessWidenerPath = rootProject.file("src/main/resources/template.accesswidener")

    decompilerOptions.named("vineflower") {
        options.put("mark-corresponding-synthetics", "1") // Adds names to lambdas - useful for mixins
    }

    runConfigs.all {
        ideConfigGenerated(true)
        vmArgs("-Dmixin.debug.export=true") // Exports transformed classes for debugging
        runDir = "../../run" // Shares the run directory between versions
    }
}

// We need java 21 (>=17) to make fletching table do mixin stuff, so we java 21 all the stuff
java {
    withSourcesJar()

    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
    targetCompatibility = JavaVersion.VERSION_21
    sourceCompatibility = JavaVersion.VERSION_21
}

kotlin {
    jvmToolchain(21)
}

// Combining this compileJava and TARGET_JVM_VERSION_ATTRIBUTE seems to let us compile the mod for the correct java
// but avoid fletching table panicking about java <17
tasks.named<JavaCompile>("compileJava") {
    options.release.set(minecraftJava)
}

configurations.configureEach {
    attributes.attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 21)
}

fletchingTable {
    mixins.create("main") {
        mixin("default", "hermes.mixins.json") {
            env("SERVER", "me.duncanruns.hermes.mixin.server")
            env("CLIENT", "me.duncanruns.hermes.mixin.client")
        }
    }
    mixins.all {
        automatic = true
    }
}

tasks {
    processResources {
        val fmjProps = mapOf(
            "id" to project.property("mod.id"),
            "name" to project.property("mod.name"),
            "version" to version.toString(),
            "minecraft" to project.property("mod.mc_dep")
        )
        inputs.properties(fmjProps)
        filesMatching("fabric.mod.json") {
            expand(fmjProps)
        }

        val mixinProps = mapOf(
            "java" to minecraftJava
        )
        inputs.properties(mixinProps)
        filesMatching("hermes.mixins.json") {
            expand(mixinProps)
        }
    }

    // Builds the version into a shared folder in `build/libs/${mod version}/`
    register<Copy>("buildAndCollect") {
        group = "build"

        into(rootProject.layout.buildDirectory.dir("libs/${project.property("mod.version")}"))

        from(remapJar.map { it.archiveFile }) {
            into(stonecutter.current.version)
        }

        from(remapSourcesJar.map { it.archiveFile }) {
            into("sources")
        }

        dependsOn("build")
    }
}
