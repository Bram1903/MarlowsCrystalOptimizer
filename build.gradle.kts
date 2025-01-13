plugins {
    alias(libs.plugins.fabric.loom)
    `maven-publish`
}

version = "1.0.0-SNAPSHOT"
group = "com.marlowcrystal"

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.officialMojangMappings())
    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.api)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}

tasks {
    java {
        disableAutoTargetJvm()
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    }

    withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.release = 21
    }

    processResources {
        inputs.property("version", project.version)
        inputs.property("minecraft_version", libs.versions.minecraft.version.get())
        inputs.property("loader_version", libs.versions.loader.version.get())
        filteringCharset = "UTF-8"

        filesMatching("fabric.mod.json") {
            expand(
                "version" to project.version,
                "minecraft_version" to libs.versions.minecraft.version.get(),
                "loader_version" to libs.versions.loader.version.get()
            )
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}
