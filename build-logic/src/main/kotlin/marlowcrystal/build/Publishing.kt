package marlowcrystal.build

import me.modmuss50.mpp.ModPublishExtension
import me.modmuss50.mpp.ReleaseType
import org.gradle.api.Project

fun ModPublishExtension.releaseDefaults(project: Project) {
    dryRun.set(project.providers.environmentVariable("MCO_PUBLISH_DRY_RUN").isPresent)
    changelog.set(project.providers.fileContents(project.rootProject.layout.projectDirectory.file("CHANGELOG.md")).asText)
    type.set(ReleaseType.STABLE)
}
