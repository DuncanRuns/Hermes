plugins {
    id("dev.kikugie.loom-back-compat")
    id("dev.kikugie.fletching-table.fabric") version "0.1.0-alpha.22"
}

version = "${property("mod.version")}+MC${stonecutter.current.version}"
base.archivesName = property("mod.id") as String

val requiredJava = when {
    stonecutter.eval(stonecutter.current.version, ">=26.1") -> JavaVersion.VERSION_25
    else -> JavaVersion.VERSION_25
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
    targetCompatibility = requiredJava
    sourceCompatibility = requiredJava
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

        from(loomx.modJar.map { it.archiveFile }) {
            into(stonecutter.current.version)
        }

        from(loomx.modSourcesJar.map { it.archiveFile }) {
            into("sources")
        }

        dependsOn("build")
    }
}
