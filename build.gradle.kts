plugins {
    alias(libs.plugins.fabric.loom)
    `maven-publish`
}

version = "1.0.1"
group = "com.marlowcrystal"

base {
    archivesName.set("${project.name}-${libs.versions.minecraft.get()}.X")
}

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.officialMojangMappings())
    modImplementation(libs.fabric.loader)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}

tasks {
    java {
        disableAutoTargetJvm()
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    }

    jar {
        from("LICENSE") {
            rename { "${it}_${project.base.archivesName.get()}" }
        }
    }

    withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.release = 17
    }

    processResources {
        inputs.property("version", project.version)
        inputs.property("loader_version", libs.versions.fabric.loader.get())
        inputs.property("minecraft_version", libs.versions.minecraft.get())
        filteringCharset = "UTF-8"

        filesMatching("fabric.mod.json") {
            expand(
                "version" to project.version,
                "loader_version" to libs.versions.fabric.loader.get(),
                "minecraft_version" to libs.versions.minecraft.get()
            )
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = project.base.archivesName.get()
            from(components["java"])
        }
    }
}