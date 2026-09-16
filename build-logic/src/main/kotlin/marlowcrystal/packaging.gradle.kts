package marlowcrystal

import marlowcrystal.build.isFabric
import marlowcrystal.build.loader
import marlowcrystal.build.modJarFile
import marlowcrystal.build.stonecutterProperty

plugins {
    java
}

// Fabric jars kept their names from before NeoForge support.
version = "${stonecutterProperty("mod.version")}+mc${stonecutterProperty("mod.mc_range")}" + if (isFabric) "" else "-$loader"
base.archivesName = stonecutterProperty("mod.archive")

tasks.withType<Jar>().configureEach {
    from(rootProject.file("LICENSE")) { rename { "${it}_${base.archivesName.get()}" } }
}

tasks.register<Copy>("buildAndCollect") {
    group = "build"
    dependsOn("build")
    from(modJarFile)
    into(rootProject.layout.buildDirectory.dir("libs"))
}
