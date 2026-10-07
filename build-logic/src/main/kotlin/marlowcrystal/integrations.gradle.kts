package marlowcrystal

import marlowcrystal.build.isFabric
import marlowcrystal.build.loader
import marlowcrystal.build.stonecutterProperty
import marlowcrystal.build.stonecutterPropertyOrNull

// Exclusive, because the loader plugins add their repositories first and one 5xx from those fails the whole build.
repositories {
    exclusiveContent {
        forRepository { maven("https://maven.terraformersmc.com/releases") { name = "TerraformersMC" } }
        filter { includeGroup("com.terraformersmc") }
    }
    exclusiveContent {
        forRepository { maven("https://maven.isxander.dev/releases") { name = "Xander Maven" } }
        filter { includeGroup("dev.isxander") }
    }
}

dependencies {
    val integration = if (isFabric) "modCompileOnly" else "compileOnly"
    if (isFabric) {
        integration("com.terraformersmc:modmenu:${stonecutterProperty("deps.modmenu")}") { isTransitive = false }
    }
    stonecutterPropertyOrNull("deps.yacl")?.let { yacl ->
        integration("dev.isxander:yet-another-config-lib:$yacl-$loader") { isTransitive = false }
    }
}
