package marlowcrystal

import marlowcrystal.build.isFabric
import marlowcrystal.build.requiredJava
import marlowcrystal.build.sc
import marlowcrystal.build.stonecutterProperty
import marlowcrystal.build.stonecutterPropertyOrNull

plugins {
    java
}

tasks.processResources {
    val hasYacl = stonecutterPropertyOrNull("deps.yacl") != null
    val mixinJava = "JAVA_${requiredJava.majorVersion}"
    val props = mapOf(
        "id" to stonecutterProperty("mod.id"),
        "name" to stonecutterProperty("mod.name"),
        "version" to stonecutterProperty("mod.version"),
        "minecraft" to stonecutterProperty("mod.mc_compat"),
    ) + if (isFabric) {
        mapOf(
            "loader" to stonecutterProperty("deps.fabric_loader"),
            "fabric_api" to if (sc.current.parsed < "1.20.2") "fabric" else "fabric-api",
        )
    } else {
        mapOf("neoforge" to stonecutterProperty("deps.neoforge"))
    }
    // Before 1.20.5 NeoForge reads mods.toml and still knows displayTest.
    val legacyNeoForge = sc.current.parsed < "1.20.5"

    inputs.properties(props)
    inputs.property("yacl", hasYacl)
    inputs.property("mixinJava", mixinJava)

    if (isFabric) exclude("META-INF/neoforge.mods.toml") else exclude("fabric.mod.json")

    filesMatching("fabric.mod.json") {
        expand(props)
        if (!hasYacl) filter { line -> line.takeUnless { "yet_another_config_lib_v3" in it } }
    }
    filesMatching("META-INF/neoforge.mods.toml") {
        expand(props)
        if (legacyNeoForge) name = "mods.toml" else filter { line -> line.takeUnless { it.startsWith("displayTest") } }
    }
    filesMatching("*.mixins.json") { expand("java" to mixinJava) }
}
