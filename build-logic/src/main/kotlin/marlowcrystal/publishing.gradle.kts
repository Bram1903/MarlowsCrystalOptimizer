package marlowcrystal

import marlowcrystal.build.modJarFile
import marlowcrystal.build.releaseDefaults
import marlowcrystal.build.requiredJava
import marlowcrystal.build.stonecutterList
import marlowcrystal.build.stonecutterProperty
import marlowcrystal.build.stonecutterPropertyOrNull
import me.modmuss50.mpp.PublishModTask

plugins {
    id("me.modmuss50.mod-publish-plugin")
}

val releaseTargets = stonecutterList("mod", "mc_releases")

require(releaseTargets.isNotEmpty()) {
    "mod.mc_releases is missing for ${project.name} in stonecutter.properties.toml"
}

val hasYacl = stonecutterPropertyOrNull("deps.yacl") != null

tasks.withType<PublishModTask>().configureEach {
    dependsOn(rootProject.tasks.named("checkPublishSecrets"))
}

publishMods {
    releaseDefaults(project, stonecutterProperty("mod.version"))
    file = modJarFile
    version = providers.provider { project.version.toString() }
    displayName = "${stonecutterProperty("mod.name")} ${stonecutterProperty("mod.version")} for ${stonecutterProperty("mod.mc_range")}"
    modLoaders.add("fabric")

    modrinth {
        accessToken = providers.environmentVariable("MODRINTH_TOKEN")
        projectId = "ozpC8eDC"
        minecraftVersions.addAll(releaseTargets)
        // Unset, Modrinth silently takes whatever the project page says.
        environment = CLIENT_ONLY
        requires("fabric-api")
        optional("modmenu")
        if (hasYacl) optional("yacl")
    }

    curseforge {
        accessToken = providers.environmentVariable("CURSEFORGE_TOKEN")
        projectId = "1554587"
        // The plugin cannot link an uploaded CurseForge file without it.
        projectSlug = "marlow-crystal-optimizer"
        minecraftVersions.addAll(releaseTargets)
        client = true
        server = false
        javaVersions.add(requiredJava)
        requires("fabric-api")
        optional("modmenu")
        if (hasYacl) optional("yacl")
    }

    github {
        accessToken = providers.environmentVariable("GITHUB_TOKEN")
        parent(rootProject.tasks.named("publishGithub"))
    }
}
