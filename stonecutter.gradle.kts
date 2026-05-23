plugins {
    id("dev.kikugie.stonecutter")
    id("fabric-loom") version "1.16-SNAPSHOT" apply false
}

stonecutter active "1.14.4"

// See https://stonecutter.kikugie.dev/wiki/config/params
stonecutter parameters {
    swaps["mod_version"] = "\"" + property("mod.version") + "\";"
    swaps["minecraft"] = "\"" + node.metadata.version + "\";"
    constants["release"] = property("mod.id") != "template"
}
