import me.modmuss50.mpp.ReleaseType

plugins {
    id("dev.kikugie.stonecutter")
    id("me.modmuss50.mod-publish-plugin") version "2.2.0"
}

stonecutter active "26.1"

stonecutter parameters {
    replacements {
        string(current.parsed >= "1.21.5") {
            replace("MobEffects.DAMAGE_BOOST", "MobEffects.STRENGTH")
        }
        string(current.parsed >= "1.21.11") {
            replace("ResourceLocation", "Identifier")
        }
        string(current.parsed >= "26.1") {
            replace("PayloadTypeRegistry.configurationS2C()", "PayloadTypeRegistry.clientboundConfiguration()")
        }
        string(current.parsed >= "26.1") {
            replace("PayloadTypeRegistry.playS2C()", "PayloadTypeRegistry.clientboundPlay()")
        }
        string(current.parsed >= "26.1") {
            replace("PayloadTypeRegistry.playC2S()", "PayloadTypeRegistry.serverboundPlay()")
        }
    }
}


publishMods {
    dryRun = providers.environmentVariable("MCO_PUBLISH_DRY_RUN").isPresent
    changelog = providers.fileContents(rootProject.layout.projectDirectory.file("CHANGELOG.md")).asText
    type = ReleaseType.STABLE
    version = property("mod.version") as String
    displayName = "v${property("mod.version")}"

    github {
        accessToken = providers.environmentVariable("GITHUB_TOKEN")
        repository = "Bram1903/MarlowsCrystalOptimizer"
        commitish = "main"
        tagName = "v${property("mod.version")}"
        allowEmptyFiles = true
    }
}
