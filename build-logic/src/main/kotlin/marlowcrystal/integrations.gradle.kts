package marlowcrystal

import marlowcrystal.build.isFabric
import marlowcrystal.build.loader
import marlowcrystal.build.stonecutterProperty
import marlowcrystal.build.stonecutterPropertyOrNull

repositories {
    maven("https://maven.terraformersmc.com/releases") { name = "TerraformersMC" }
    maven("https://maven.isxander.dev/releases") { name = "Xander Maven" }
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
