package marlowcrystal

import marlowcrystal.build.minecraftDependency
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
        "minecraft" to minecraftDependency,
        "loader" to stonecutterProperty("deps.fabric_loader"),
        "fabric_api" to if (sc.current.parsed < "1.20.2") "fabric" else "fabric-api",
    )

    inputs.properties(props)
    inputs.property("yacl", hasYacl)
    inputs.property("mixinJava", mixinJava)

    filesMatching("fabric.mod.json") {
        expand(props)
        if (!hasYacl) filter { line -> line.takeUnless { "yet_another_config_lib_v3" in it } }
    }
    filesMatching("*.mixins.json") { expand("java" to mixinJava) }
}
