import marlowcrystal.build.MinecraftArtifactsLock

plugins {
    id("net.neoforged.moddev")
    id("marlowcrystal.toolchain")
    id("marlowcrystal.versioning")
    id("marlowcrystal.resources")
    id("marlowcrystal.integrations")
    id("marlowcrystal.packaging")
    id("marlowcrystal.publishing")
}

neoForge {
    version = sc.properties.getOrNull<String>("deps.neoforge_build") ?: sc.properties.get<String>("deps.neoforge")

    mods {
        register(sc.properties.get<String>("mod.id")) {
            sourceSet(sourceSets.main.get())
        }
    }

    runs {
        register("client") {
            client()
            gameDirectory = rootProject.file("run/neoforge")
        }
    }
}

// Decompiling Minecraft for several versions at once exhausts memory.
val minecraftArtifactsLock = gradle.sharedServices.registerIfAbsent("minecraftArtifacts", MinecraftArtifactsLock::class) {
    maxParallelUsages = 1
}

tasks.named("createMinecraftArtifacts") {
    usesService(minecraftArtifactsLock)
    dependsOn("stonecutterGenerate")
}
