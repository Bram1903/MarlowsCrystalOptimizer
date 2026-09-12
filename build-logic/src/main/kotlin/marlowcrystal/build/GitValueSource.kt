package marlowcrystal.build

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import javax.inject.Inject

abstract class GitValueSource : ValueSource<String, GitValueSource.Parameters> {

    interface Parameters : ValueSourceParameters {
        val workingDirectory: DirectoryProperty
        val arguments: ListProperty<String>
    }

    @get:Inject
    abstract val execOperations: ExecOperations

    override fun obtain(): String? {
        val output = ByteArrayOutputStream()
        val result = runCatching {
            execOperations.exec {
                workingDir = parameters.workingDirectory.get().asFile
                commandLine(listOf("git") + parameters.arguments.get())
                standardOutput = output
                errorOutput = ByteArrayOutputStream()
                isIgnoreExitValue = true
            }
        }.getOrNull() ?: return null

        if (result.exitValue != 0) return null
        return output.toString(Charsets.UTF_8).trim().ifEmpty { null }
    }
}
