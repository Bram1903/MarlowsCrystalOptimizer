plugins {
    id("dev.kikugie.loom-back-compat")
    id("marlowcrystal.toolchain")
    id("marlowcrystal.versioning")
    id("marlowcrystal.resources")
    id("marlowcrystal.integrations")
    id("marlowcrystal.packaging")
    id("marlowcrystal.publishing")
}

dependencies {
    minecraft("com.mojang:minecraft:${sc.current.version}")
    loomx.applyMojangMappings()

    modImplementation("net.fabricmc:fabric-loader:${sc.properties.get<String>("deps.fabric_loader")}")
    modImplementation(fabricApi.module("fabric-networking-api-v1", sc.properties.get<String>("deps.fabric_api")))
}

loom {
    runConfigs.all {
        preferGradleTask = true
        generateRunConfig = true
        runDirectory = rootProject.file("run")
    }
}
