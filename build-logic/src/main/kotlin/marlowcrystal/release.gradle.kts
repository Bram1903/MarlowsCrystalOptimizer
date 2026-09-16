package marlowcrystal

import marlowcrystal.build.PUBLISH_SECRETS
import marlowcrystal.build.ReleasedJar
import marlowcrystal.build.discordAnnouncement
import marlowcrystal.build.isPublishDryRun
import marlowcrystal.build.loader
import marlowcrystal.build.minecraftVersionOrder
import marlowcrystal.build.releaseDefaults
import marlowcrystal.build.stonecutterList
import me.modmuss50.mpp.PublishModTask
import java.util.concurrent.Callable

plugins {
    id("me.modmuss50.mod-publish-plugin")
}

val modVersion = property("mod.version") as String
val publishDryRun = isPublishDryRun
val githubRepository = "Bram1903/MarlowsCrystalOptimizer"
val releaseTag = "v$modVersion"
// Built rather than read from the upload result, whose dry run link points at the plugin's own repository.
val githubRelease = "https://github.com/$githubRepository/releases/tag/$releaseTag"
val iconUrl = "https://raw.githubusercontent.com/$githubRepository/main/src/main/resources/assets/marlows-crystal-optimizer/icon.png"

// Runs before every upload, so a missing secret cannot leave a release on only some platforms.
val checkPublishSecrets = tasks.register("checkPublishSecrets") {
    group = "publishing"
    val secrets = PUBLISH_SECRETS.associateWith { providers.environmentVariable(it) }
    onlyIf("publishing for real") { !publishDryRun }
    doLast {
        val missing = secrets.filterValues { !it.isPresent }.keys
        check(missing.isEmpty()) { "Cannot publish, these environment variables are not set: ${missing.joinToString()}" }
    }
}

tasks.withType<PublishModTask>().configureEach {
    dependsOn(checkPublishSecrets)
}

// Modrinth and CurseForge list the latest upload first, so the newest Minecraft versions go last, and for a range
// both loaders support Fabric goes after NeoForge. Without this, parallel execution uploads in any order.
val uploadOrder = compareBy(minecraftVersionOrder) { jar: Project -> jar.name.substringBefore('-') }
    .thenByDescending { it.name.substringAfter('-') }
subprojects.sortedWith(uploadOrder).zipWithNext { previous, next ->
    next.tasks.withType<PublishModTask>().configureEach { mustRunAfter(previous.tasks.withType<PublishModTask>()) }
}

publishMods {
    releaseDefaults(project, modVersion)
    version = modVersion
    displayName = releaseTag

    github {
        accessToken = providers.environmentVariable("MCO_GITHUB_TOKEN")
        repository = githubRepository
        commitish = "main"
        tagName = releaseTag
        allowEmptyFiles = true
    }

    val releaseNotes = changelog

    // Here rather than in the version projects, which would post one announcement per jar.
    discord {
        // The jars attached to the GitHub release are separate tasks and would otherwise land after the announcement.
        dependsOn(tasks.withType<PublishModTask>(), Callable { subprojects.map { it.tasks.withType<PublishModTask>() } })

        // A required input, but dry runs must work without secrets.
        webhookUrl = providers.environmentVariable("MCO_DISCORD_WEBHOOK")
            .let { if (publishDryRun) it.orElse("unused during a dry run") else it }
        dryRunWebhookUrl = providers.environmentVariable("MCO_DISCORD_WEBHOOK_DRY_RUN")
        username = "Marlow's Crystal Optimizer"
        avatarUrl = iconUrl

        // The plugin posts a separate card per upload; the content lists the downloads instead.
        publishResults.setFrom()

        content = providers.provider {
            val jars = subprojects.map { ReleasedJar(it.loader, it.stonecutterList("mod", "mc_releases")) }
            discordAnnouncement(modVersion, releaseNotes.get(), jars, githubRelease)
        }

        style {
            // CLASSIC sends the text as message content, which Discord caps at 2000 characters; an embed holds 4096.
            look = "MODERN"
            color = "#D451EA"
            thumbnailUrl = iconUrl
        }
    }
}
