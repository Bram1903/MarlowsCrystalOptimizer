package marlowcrystal

import marlowcrystal.build.releaseDefaults

plugins {
    id("me.modmuss50.mod-publish-plugin")
}

val modVersion = property("mod.version") as String

publishMods {
    releaseDefaults(project)
    version = modVersion
    displayName = "v$modVersion"

    github {
        accessToken = providers.environmentVariable("GITHUB_TOKEN")
        repository = "Bram1903/MarlowsCrystalOptimizer"
        commitish = "main"
        tagName = "v$modVersion"
        allowEmptyFiles = true
    }
}
