package marlowcrystal

import marlowcrystal.build.stonecutterProperty
import marlowcrystal.build.stonecutterPropertyOrNull

repositories {
    maven("https://maven.terraformersmc.com/releases") { name = "TerraformersMC" }
    maven("https://maven.isxander.dev/releases") { name = "Xander Maven" }
}

dependencies {
    "modCompileOnly"("com.terraformersmc:modmenu:${stonecutterProperty("deps.modmenu")}") { isTransitive = false }
    stonecutterPropertyOrNull("deps.yacl")?.let { yacl ->
        "modCompileOnly"("dev.isxander:yet-another-config-lib:$yacl") { isTransitive = false }
    }
}
