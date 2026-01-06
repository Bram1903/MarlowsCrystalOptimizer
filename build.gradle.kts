plugins {
    alias(libs.plugins.fabric.loom)
    `maven-publish`
}

version = "1.0.6-SNAPSHOT"
group = "com.deathmotion.marlowcrystal"

base {
    archivesName.set("${project.name}-${libs.versions.minecraft.get()}")
}

dependencies {
    minecraft(libs.minecraft)
    implementation(libs.fabric.loader)
    implementation(libs.fabric.api)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}

tasks {
    java {
        disableAutoTargetJvm()
        toolchain.languageVersion.set(JavaLanguageVersion.of(25))
    }

    jar {
        from("LICENSE") {
            rename { "${it}_${project.base.archivesName.get()}" }
        }
    }

    withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.release = 25
    }

    processResources {
        inputs.property("version", project.version)
        inputs.property("loader_version", libs.versions.fabric.loader.get())
        filteringCharset = "UTF-8"

        filesMatching("fabric.mod.json") {
            expand(
                "version" to project.version,
                "loader_version" to libs.versions.fabric.loader.get()
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