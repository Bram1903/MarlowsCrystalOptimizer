import me.modmuss50.mpp.ReleaseType

plugins {
    id("dev.kikugie.loom-back-compat")
    id("me.modmuss50.mod-publish-plugin")
}

version = "${property("mod.version")}+mc${property("mod.mc_range")}"
base.archivesName = property("mod.archive") as String

val releaseTargets: List<String> = sc.properties.rawOrNull("mod", "mc_releases")
    ?.asList().orEmpty().map { it.toString() }

require(releaseTargets.isNotEmpty()) {
    "mod.mc_releases is missing for ${sc.current.version} in stonecutter.properties.toml"
}

val requiredJava: JavaVersion = when {
    sc.current.parsed >= "26.1" -> JavaVersion.VERSION_25
    sc.current.parsed >= "1.20.5" -> JavaVersion.VERSION_21
    else -> JavaVersion.VERSION_17
}

fun git(vararg arguments: String): String =
    runCatching {
        providers.exec {
            workingDir = rootProject.projectDir
            commandLine("git", *arguments)
            isIgnoreExitValue = true
        }.standardOutput.asText.get().trim()
    }.getOrDefault("")

val modVersion = sc.properties.get<String>("mod.version")
val modVersionParts = requireNotNull(Regex("""(\d+)\.(\d+)\.(\d+)(-SNAPSHOT)?""").matchEntire(modVersion)) {
    "mod.version must be major.minor.patch with an optional -SNAPSHOT suffix, found $modVersion"
}
val gitCommit: String? = git("rev-parse", "HEAD").ifEmpty { null }
val gitDirty: Boolean = gitCommit != null && git("status", "--porcelain", "--untracked-files=no").isNotEmpty()

val generateVersions = tasks.register("generateVersions") {
    fun quoted(value: String?) = value?.let { "\"$it\"" } ?: "null"

    val (major, minor, patch, snapshot) = modVersionParts.destructured
    val source = """
        package com.deathmotion.marlowcrystal.versioning;

        public final class MCOVersions {

            public static final String RAW = "$modVersion";
            public static final String MINECRAFT_RANGE = "${sc.properties.get<String>("mod.mc_range")}";
            public static final String COMMIT = ${quoted(gitCommit)};
            public static final boolean DIRTY = $gitDirty;
            public static final MCOVersion CURRENT = new MCOVersion($major, $minor, $patch, ${snapshot.isNotEmpty()}, ${quoted(gitCommit?.take(7))});
            public static final MCOVersion UNKNOWN = MCOVersion.of(0, 0, 0);

            private MCOVersions() {
                throw new IllegalStateException();
            }
        }
    """.trimIndent() + "\n"

    val target = layout.buildDirectory.dir("generated/sources/versions/main")
    inputs.property("source", source)
    outputs.dir(target)

    doLast {
        val file = target.get().file("com/deathmotion/marlowcrystal/versioning/MCOVersions.java").asFile
        file.parentFile.mkdirs()
        file.writeText(source)
    }
}

sourceSets.main {
    java.srcDir(generateVersions)
}

val yaclVersion: String? = sc.properties.getOrNull<String>("deps.yacl")

repositories {
    maven("https://maven.terraformersmc.com/releases") { name = "TerraformersMC" }
    maven("https://maven.isxander.dev/releases") { name = "Xander Maven" }
}

dependencies {
    fun fapi(vararg modules: String) {
        for (it in modules) modImplementation(fabricApi.module(it, sc.properties.get<String>("deps.fabric_api")))
    }

    minecraft("com.mojang:minecraft:${sc.current.version}")
    loomx.applyMojangMappings()

    modImplementation("net.fabricmc:fabric-loader:${sc.properties.get<String>("deps.fabric_loader")}")
    fapi("fabric-networking-api-v1")

    modCompileOnly("com.terraformersmc:modmenu:${sc.properties.get<String>("deps.modmenu")}") { isTransitive = false }
    if (yaclVersion != null) {
        modCompileOnly("dev.isxander:yet-another-config-lib:$yaclVersion") { isTransitive = false }
    }
}

loom {
    runConfigs.all {
        preferGradleTask = true
        generateRunConfig = true
        runDirectory = rootProject.file("run")
    }
}

java {
    targetCompatibility = requiredJava
    sourceCompatibility = requiredJava

    toolchain {
        vendor = JvmVendorSpec.ADOPTIUM
        languageVersion = JavaLanguageVersion.of(requiredJava.majorVersion)
    }
}

tasks {
    withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.release = requiredJava.majorVersion.toInt()
    }

    processResources {
        val props = mapOf(
            "id" to sc.properties.get<String>("mod.id"),
            "name" to sc.properties.get<String>("mod.name"),
            "version" to sc.properties.get<String>("mod.version"),
            "minecraft" to sc.properties.get<String>("mod.mc_compat"),
            "loader" to sc.properties.get<String>("deps.fabric_loader"),
        )
        props.forEach { (k, v) -> inputs.property(k, v) }

        filesMatching("fabric.mod.json") { expand(props) }
        filesMatching("*.mixins.json") { expand("java" to "JAVA_${requiredJava.majorVersion}") }
    }

    withType<Jar> {
        from(rootProject.file("LICENSE")) { rename { "${it}_${base.archivesName.get()}" } }
    }

    register<Copy>("buildAndCollect") {
        group = "build"
        dependsOn("build")
        from(loomx.modJar.flatMap { it.archiveFile })
        into(rootProject.layout.buildDirectory.dir("libs"))
    }
}

publishMods {
    file = loomx.modJar.flatMap { it.archiveFile }
    version = project.version.toString()
    displayName = "${sc.properties.get<String>("mod.name")} ${sc.properties.get<String>("mod.version")} for ${sc.properties.get<String>("mod.mc_range")}"
    changelog = providers.fileContents(rootProject.layout.projectDirectory.file("CHANGELOG.md")).asText
    type = ReleaseType.STABLE
    modLoaders.add("fabric")
    dryRun = providers.environmentVariable("MCO_PUBLISH_DRY_RUN").isPresent

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
