package marlowcrystal

import marlowcrystal.build.GenerateVersionsTask
import marlowcrystal.build.GitValueSource
import marlowcrystal.build.loader
import marlowcrystal.build.stonecutterProperty

plugins {
    java
}

fun git(vararg arguments: String): Provider<String> = providers.of(GitValueSource::class.java) {
    parameters.workingDirectory = rootProject.layout.projectDirectory
    parameters.arguments = arguments.toList()
}

val generateVersions = tasks.register<GenerateVersionsTask>("generateVersions") {
    modVersion = stonecutterProperty("mod.version")
    minecraftRange = stonecutterProperty("mod.mc_range")
    loader = project.loader
    commit = git("rev-parse", "HEAD")
    dirty = git("status", "--porcelain", "--untracked-files=no").map { true }.orElse(false)
    outputDirectory = layout.buildDirectory.dir("generated/sources/versions/main")
}

sourceSets.main {
    java.srcDir(generateVersions)
}
