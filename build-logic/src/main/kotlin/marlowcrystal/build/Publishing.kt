package marlowcrystal.build

import me.modmuss50.mpp.ModPublishExtension
import me.modmuss50.mpp.ReleaseType
import org.gradle.api.Project

val PUBLISH_SECRETS = listOf("MCO_GITHUB_TOKEN", "MCO_MODRINTH_TOKEN", "MCO_CURSEFORGE_TOKEN", "MCO_DISCORD_WEBHOOK")

val Project.isPublishDryRun: Boolean
    get() = providers.environmentVariable("MCO_PUBLISH_DRY_RUN").isPresent

fun ModPublishExtension.releaseDefaults(project: Project, modVersion: String) {
    dryRun.set(project.isPublishDryRun)
    changelog.set(project.providers.fileContents(project.rootProject.layout.projectDirectory.file("CHANGELOG.md")).asText)
    type.set(if (modVersion.endsWith("-SNAPSHOT")) ReleaseType.BETA else ReleaseType.STABLE)
}

val minecraftVersionOrder: Comparator<String> = Comparator { left, right ->
    val a = left.split('.').map(String::toInt)
    val b = right.split('.').map(String::toInt)
    (0 until maxOf(a.size, b.size))
        .map { a.getOrElse(it) { 0 }.compareTo(b.getOrElse(it) { 0 }) }
        .firstOrNull { it != 0 } ?: 0
}

// CurseForge filters its file list by these mod loader ids.
private class Loader(val displayName: String, val curseforgeId: Int)

private val LOADERS = mapOf("fabric" to Loader("Fabric", 4), "neoforge" to Loader("NeoForge", 6))

fun loaderName(loader: String): String = LOADERS[loader]?.displayName ?: error("Unknown loader $loader")

data class ReleasedJar(val loader: String, val minecraftReleases: List<String>)

private const val MODRINTH_PROJECT = "https://modrinth.com/mod/marlow-crystal-optimizer"
private const val CURSEFORGE_PROJECT = "https://www.curseforge.com/minecraft/mc-mods/marlow-crystal-optimizer"

private const val DISCORD_DESCRIPTION_LIMIT = 4096

private fun downloadLines(jars: List<ReleasedJar>): List<String> = LOADERS.mapNotNull { (id, loader) ->
    val releases = jars.filter { it.loader == id }.flatMap { it.minecraftReleases }.sortedWith(minecraftVersionOrder)
    if (releases.isEmpty()) return@mapNotNull null
    "- **${loader.displayName}** ${releases.first()}-${releases.last()} · [Modrinth]($MODRINTH_PROJECT/versions?l=$id) " +
        "[CurseForge]($CURSEFORGE_PROJECT/files/all?gameVersionTypeId=${loader.curseforgeId})"
}

fun discordAnnouncement(modVersion: String, changelog: String, jars: List<ReleasedJar>, githubRelease: String): String {
    val heading = "# Marlow's Crystal Optimizer $modVersion"
    val downloads = buildString {
        append("### Download")
        downloadLines(jars).forEach { append("\n").append(it) }
        append("\n- **Every version** · [GitHub]($githubRelease)")
    }

    // When both do not fit, the changelog is cut, since the full notes are one link away and the downloads are not.
    var notes = discordMarkdown(modVersion, changelog)
    val room = DISCORD_DESCRIPTION_LIMIT - heading.length - downloads.length - 2
    if (notes.length > room) {
        val more = "\n[Read the full changelog on GitHub]($githubRelease)"
        notes = notes.take(room - more.length).substringBeforeLast('\n') + more
    }
    return "$heading\n$notes\n$downloads"
}

// CHANGELOG.md is hard wrapped. GitHub, Modrinth and CurseForge reflow that, Discord keeps every line break.
private fun discordMarkdown(modVersion: String, changelog: String): String {
    fun startsBlock(line: String): Boolean {
        val text = line.trimStart()
        return text.isEmpty() || text.startsWith("#") || text.startsWith("- ") || text.startsWith("* ") ||
            text.startsWith("```") || Regex("""\d+\. .*""").matches(text)
    }

    val lines = mutableListOf<String>()
    var inCode = false
    for (line in changelog.trim().lines().map(String::trimEnd)) {
        if (line.trimStart().startsWith("```")) inCode = !inCode
        if (!inCode && !startsBlock(line) && lines.lastOrNull()?.isNotBlank() == true) {
            lines[lines.lastIndex] = lines.last() + " " + line.trim()
        } else {
            lines += line
        }
    }

    if (lines.firstOrNull() in setOf("## $modVersion", "## ${modVersion.removeSuffix("-SNAPSHOT")}")) lines.removeAt(0)
    // Discord already spaces headings and list items, so blank lines before them only add gaps.
    return lines
        .filterIndexed { i, line -> !(line.isBlank() && lines.getOrNull(i + 1)?.let(::startsBlock) != false) }
        .joinToString("\n")
        .trim()
}
