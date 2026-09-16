package marlowcrystal.build

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault

@DisableCachingByDefault(because = "The generated source embeds the build time")
abstract class GenerateVersionsTask : DefaultTask() {

    @get:Input
    abstract val modVersion: Property<String>

    @get:Input
    abstract val minecraftRange: Property<String>

    @get:Input
    abstract val loader: Property<String>

    @get:Input
    @get:Optional
    abstract val commit: Property<String>

    @get:Input
    abstract val dirty: Property<Boolean>

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    init {
        outputs.upToDateWhen { false }
    }

    @TaskAction
    fun generate() {
        val version = modVersion.get()
        val match = requireNotNull(VERSION.matchEntire(version)) {
            "mod.version must be major.minor.patch with an optional -SNAPSHOT suffix, found $version"
        }
        val (major, minor, patch, snapshot) = match.destructured
        val fullCommit = commit.orNull

        val file = outputDirectory.file("com/deathmotion/marlowcrystal/versioning/MCOVersions.java").get().asFile
        file.parentFile.mkdirs()
        file.writeText(
            """
            package com.deathmotion.marlowcrystal.versioning;

            import java.time.Instant;

            public final class MCOVersions {

                public static final String RAW = "$version";
                public static final String MINECRAFT_RANGE = "${minecraftRange.get()}";
                public static final ModLoader LOADER = ModLoader.${loader.get().uppercase()};
                public static final String COMMIT = ${quoted(fullCommit)};
                public static final boolean DIRTY = ${dirty.get()};
                public static final Instant BUILD_TIMESTAMP = Instant.ofEpochMilli(${System.currentTimeMillis()}L);
                public static final MCOVersion CURRENT = new MCOVersion($major, $minor, $patch, ${snapshot.isNotEmpty()}, ${quoted(fullCommit?.take(7))});
                public static final MCOVersion UNKNOWN = MCOVersion.of(0, 0, 0);

                private MCOVersions() {
                    throw new IllegalStateException();
                }
            }
            """.trimIndent() + "\n"
        )
    }

    private fun quoted(value: String?): String = value?.let { "\"$it\"" } ?: "null"

    private companion object {
        val VERSION = Regex("""(\d+)\.(\d+)\.(\d+)(-SNAPSHOT)?""")
    }
}
