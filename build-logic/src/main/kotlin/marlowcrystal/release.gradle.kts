package marlowcrystal

import marlowcrystal.build.PUBLISH_SECRETS
import marlowcrystal.build.RangeDownloads
import marlowcrystal.build.discordAnnouncement
import marlowcrystal.build.isPublishDryRun
import marlowcrystal.build.minecraftVersionOrder
import marlowcrystal.build.releaseDefaults
import marlowcrystal.build.stonecutterProperty
import me.modmuss50.mpp.PublishModTask
import me.modmuss50.mpp.PublishResult
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

// PublishResult is internal to the plugin, but it is what the plugin links with, and a change to it fails the
// build instead of posting a wrong link.
fun uploadLink(project: Project, task: String): String =
    PublishResult.fromJson(project.tasks.named<PublishModTask>(task).get().result.get().asFile.readText()).link

publishMods {
    releaseDefaults(project, modVersion)
    version = modVersion
    displayName = releaseTag

    github {
        accessToken = providers.environmentVariable("GITHUB_TOKEN")
        repository = githubRepository
        commitish = "main"
        tagName = releaseTag
        allowEmptyFiles = true
    }

    val releaseNotes = changelog

    // Here rather than in the version projects, which would post one announcement per Minecraft range.
    discord {
        // The jars attached to the GitHub release are separate tasks and would otherwise land after the announcement.
        dependsOn(tasks.withType<PublishModTask>(), Callable { subprojects.map { it.tasks.withType<PublishModTask>() } })

        // A required input, but dry runs must work without secrets.
        webhookUrl = providers.environmentVariable("DISCORD_WEBHOOK")
            .let { if (publishDryRun) it.orElse("unused during a dry run") else it }
        dryRunWebhookUrl = providers.environmentVariable("DISCORD_WEBHOOK_DRY_RUN")
        username = "Marlow's Crystal Optimizer"
        avatarUrl = iconUrl

        // The plugin posts a separate card per upload, thirteen of them; the content lists the downloads instead.
        publishResults.setFrom()

        // Upload links only exist once the uploads have run.
        content = providers.provider {
            val ranges = subprojects
                .sortedWith(compareByDescending(minecraftVersionOrder) { it.name })
                .map { range ->
                    RangeDownloads(
                        range.stonecutterProperty("mod.mc_range"),
                        listOf("Modrinth" to uploadLink(range, "publishModrinth"), "CurseForge" to uploadLink(range, "publishCurseforge")),
                    )
                }
            discordAnnouncement(modVersion, releaseNotes.get(), ranges, githubRelease)
        }

        style {
            // CLASSIC sends the text as message content, which Discord caps at 2000 characters; an embed holds 4096.
            look = "MODERN"
            color = "#D451EA"
            thumbnailUrl = iconUrl
        }
    }
}
