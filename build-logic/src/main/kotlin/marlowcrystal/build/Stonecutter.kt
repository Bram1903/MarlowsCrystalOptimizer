package marlowcrystal.build

import dev.kikugie.loomx.LoomCompatProjectExtension
import dev.kikugie.stonecutter.build.StonecutterBuildExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.named

val Project.sc: StonecutterBuildExtension
    get() = extensions.getByType()

fun Project.stonecutterProperty(key: String): String = sc.properties.get<String>(key)

fun Project.stonecutterPropertyOrNull(key: String): String? = sc.properties.getOrNull<String>(key)

fun Project.stonecutterList(vararg path: String): List<String> =
    sc.properties.rawOrNull(*path)?.asList().orEmpty().map { it.toString() }

val Project.loader: String
    get() = sc.current.project.substringAfter('-')

val Project.isFabric: Boolean
    get() = loader == "fabric"

val Project.requiredJava: JavaVersion
    get() = when {
        sc.current.parsed >= "26.1" -> JavaVersion.VERSION_25
        sc.current.parsed >= "1.20.5" -> JavaVersion.VERSION_21
        else -> JavaVersion.VERSION_17
    }

val Project.modJarFile: Provider<RegularFile>
    get() = if (isFabric) {
        extensions.getByType<LoomCompatProjectExtension>().modJar.flatMap { it.archiveFile }
    } else {
        tasks.named<Jar>("jar").flatMap { it.archiveFile }
    }
