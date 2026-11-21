plugins {
    id("dev.kikugie.stonecutter")
    id("fabric-loom") version "1.18-SNAPSHOT" apply false
}

stonecutter active "1.16.1"

// See https://stonecutter.kikugie.dev/wiki/config/params
stonecutter parameters {
    swaps["mod_version"] = "\"" + property("mod.version") + "\";"
    swaps["minecraft"] = "\"" + node.metadata.version + "\";"
    constants["release"] = property("mod.id") != "template"
}

tasks.matching {
    it.name.startsWith("Set active project to ") || it.name.startsWith("stonecutterSwitchTo")
}.configureEach {
    val version = if (name.startsWith("Set active project to ")) {
        name.removePrefix("Set active project to ").trim()
    } else {
        name.removePrefix("stonecutterSwitchTo")
    }
    finalizedBy(":$version:applyImports")
}
