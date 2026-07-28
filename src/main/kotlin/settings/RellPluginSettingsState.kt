package net.postchain.rellide.jetbrains.settings

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.util.SystemInfo
import com.intellij.util.xmlb.XmlSerializerUtil
import java.io.File

/**
 * Supports storing the plugin settings in a persistent way.
 * The [State] and [Storage] annotations define the name of the data and the file name where
 * these persistent application settings are stored.
 */
@State(
    name = "net.postchain.rellide.jetbrains.settings.RellPluginSettingsState",
    storages = [Storage("RellPluginSettings.xml")]
)
class RellPluginSettingsState : PersistentStateComponent<RellPluginSettingsState> {
    var indexCaching: Boolean = true

    /**
     * The Chromia CLI invocation. May be a plain path (e.g. `/opt/homebrew/bin/chr`) or a full
     * shell command such as `docker run ... chr`. The value is always executed through the system
     * shell, so `$(pwd)` and similar constructs are evaluated. If blank, the auto-detected path
     * is used at runtime; if nothing is detected, `chr` on PATH is assumed.
     */
    var chromiaCliCommand: String = ""

    /** Legacy field name; migrated to [chromiaCliCommand] on load. */
    var chromiaCliExecutable: String = ""

    override fun getState(): RellPluginSettingsState = this

    override fun loadState(state: RellPluginSettingsState) {
        XmlSerializerUtil.copyBean(state, this)
        if (chromiaCliCommand.isBlank() && chromiaCliExecutable.isNotBlank()) {
            chromiaCliCommand = chromiaCliExecutable
            chromiaCliExecutable = ""
        }
    }

    /**
     * Builds a [GeneralCommandLine] that invokes the configured Chromia CLI with the given [args],
     * wrapped in the system shell. If [overrideCommand] is non-blank it replaces the stored command
     * for this call (used by per-run configurations). Returns null when no command is configured
     * and nothing was auto-detected.
     */
    fun buildChromiaCliCommandLine(
        args: List<String>,
        overrideCommand: String? = null,
    ): GeneralCommandLine? {
        val cmd = overrideCommand?.trim()?.takeIf { it.isNotBlank() }
            ?: effectiveCommand()
            ?: return null
        val fullCmd = buildString {
            append(cmd)
            for (arg in args) {
                append(' ')
                append(shellQuote(arg))
            }
        }
        return wrapInShell(fullCmd)
    }

    /**
     * Builds a [GeneralCommandLine] from a full `chr ...` command string (as composed by the
     * Chromia tool window). The leading `chr` token is replaced with the effective CLI command and
     * the whole line is run through the system shell, so any shell constructs in [command] are
     * preserved. Returns null when no command is configured and nothing was auto-detected.
     */
    fun buildChromiaCliCommandLineFromString(command: String): GeneralCommandLine? {
        val substitution = effectiveCommand() ?: return null
        val prefix = "chr"
        val fullCmd = if (command == prefix || command.startsWith("$prefix ")) {
            command.replaceFirst(prefix, substitution)
        } else {
            command
        }
        return wrapInShell(fullCmd)
    }

    private fun effectiveCommand(): String? =
        chromiaCliCommand.trim().takeIf { it.isNotBlank() } ?: detectChromiaCliPath()

    companion object {
        val instance: RellPluginSettingsState
            get() = ApplicationManager.getApplication().getService(RellPluginSettingsState::class.java)

        /** Known default install locations of the Chromia CLI, in preference order. */
        fun autoDetectCandidates(): List<String> = when {
            SystemInfo.isMac -> listOf(
                "/opt/homebrew/bin/chr",
                "/usr/local/bin/chr",
            )

            SystemInfo.isWindows -> {
                val userProfile = System.getenv("USERPROFILE")
                if (userProfile.isNullOrBlank()) emptyList()
                else listOf("""$userProfile\scoop\apps\chr\current\chr.exe""")
            }

            else -> listOfNotNull(
                "/usr/local/bin/chr",
                "/usr/bin/chr",
                System.getProperty("user.home")?.let { "$it/.local/bin/chr" },
            )
        }

        fun detectChromiaCliPath(): String? {
            autoDetectCandidates().find { File(it).canExecute() }?.let { return it }
            return findOnPath(if (SystemInfo.isWindows) "chr.exe" else "chr")
        }

        fun isDockerAvailable(): Boolean =
            findOnPath(if (SystemInfo.isWindows) "docker.exe" else "docker") != null

        /** Suggested `docker run` command line for the currently running OS. */
        fun dockerCliCommand(): String = if (SystemInfo.isWindows) {
            """docker run --rm -v "%cd%:%cd%" -w "%cd%" """ +
                    "registry.gitlab.com/chromaway/core-tools/chromia-cli/chr:latest chr"
        } else {
            """docker run --rm -v "$(pwd):$(pwd)" -w "$(pwd)" """ +
                    "registry.gitlab.com/chromaway/core-tools/chromia-cli/chr:latest chr"
        }

        private fun wrapInShell(fullCmd: String): GeneralCommandLine =
            if (SystemInfo.isWindows) GeneralCommandLine("cmd", "/c", fullCmd)
            else GeneralCommandLine("sh", "-c", fullCmd)

        private fun findOnPath(name: String): String? {
            val pathEnv = System.getenv("PATH") ?: return null
            for (dir in pathEnv.split(File.pathSeparator)) {
                val f = File(dir, name)
                if (f.canExecute()) return f.absolutePath
            }
            return null
        }

        private fun shellQuote(arg: String): String =
            if (SystemInfo.isWindows) cmdQuote(arg) else posixQuote(arg)

        private fun posixQuote(arg: String): String {
            if (arg.isEmpty()) return "''"
            if (arg.all { it.isLetterOrDigit() || it in "._-/=:,@+" }) return arg
            return "'" + arg.replace("'", "'\\''") + "'"
        }

        private fun cmdQuote(arg: String): String {
            if (arg.isEmpty()) return "\"\""
            if (arg.all { it.isLetterOrDigit() || it in "._-/\\=:,@+" }) return arg
            return "\"" + arg.replace("\"", "\"\"") + "\""
        }
    }
}
