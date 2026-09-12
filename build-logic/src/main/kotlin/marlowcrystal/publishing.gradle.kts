package marlowcrystal

import marlowcrystal.build.modJarFile
import marlowcrystal.build.releaseDefaults
import marlowcrystal.build.stonecutterList
import marlowcrystal.build.stonecutterProperty

plugins {
    id("me.modmuss50.mod-publish-plugin")
}

val releaseTargets = stonecutterList("mod", "mc_releases")

require(releaseTargets.isNotEmpty()) {
    "mod.mc_releases is missing for ${project.name} in stonecutter.properties.toml"
}

publishMods {
    releaseDefaults(project)
    file = modJarFile
    version = providers.provider { project.version.toString() }
    displayName = "${stonecutterProperty("mod.name")} ${stonecutterProperty("mod.version")} for ${stonecutterProperty("mod.mc_range")}"
    modLoaders.add("fabric")

    modrinth {
        accessToken = providers.environmentVariable("MODRINTH_TOKEN")
        projectId = "ozpC8eDC"
        minecraftVersions.addAll(releaseTargets)
        requires("fabric-api")
    }

    github {
        accessToken = providers.environmentVariable("GITHUB_TOKEN")
        parent(rootProject.tasks.named("publishGithub"))
    }
}
