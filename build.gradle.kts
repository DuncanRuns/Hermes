plugins {
    id("fabric-loom")
    kotlin("jvm") version "2.2.10"
    id("com.google.devtools.ksp") version "2.2.10-2.0.2"
    id("dev.kikugie.fletching-table.fabric") version "0.1.0-alpha.22"
}

version = "${property("mod.version")}+MC${stonecutter.current.version}"
base.archivesName = property("mod.id") as String

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
    }else {
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

java {
    withSourcesJar()

    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(
        when {
            stonecutter.eval(stonecutter.current.version, ">=1.20.6") -> 21
            stonecutter.eval(stonecutter.current.version, ">=1.18") -> 17
            stonecutter.eval(stonecutter.current.version, ">=1.17") -> 16
            else -> 8
        }
    )
}

fletchingTable {
    j52j.register("main") {
        extension("json", "hermes.mixins.json5")
    }
}

tasks {
    processResources {
        val props = mapOf(
            "id" to project.property("mod.id"),
            "name" to project.property("mod.name"),
            "version" to version.toString(),
            "minecraft" to project.property("mod.mc_dep")
        )

        inputs.properties(props)

        filesMatching("fabric.mod.json") {
            expand(props)
            filter { line ->
                line.replace(".mixins.json5", ".mixins.json")
            }
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
