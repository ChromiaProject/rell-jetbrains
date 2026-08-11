package net.postchain.rellide.jetbrains.settings

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.util.SystemInfo
import com.intellij.util.xmlb.XmlSerializerUtil
import net.postchain.rellide.jetbrains.chromia.RellVersion
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

    /** The CLI command whose `--version` output [chrVersionOutput] came from. */
    var chrVersionCommand: String = ""

    /** Raw output of the last successful `chr --version` run — a background probe or the settings Test button. */
    var chrVersionOutput: String = ""

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
                append(' ').append(shellQuote(arg))
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

    /**
     * Remembers the `--version` output of a successful run. [command] is the exact CLI command the
     * run used, or null when it ran the effective command.
     */
    fun recordChrVersionOutput(command: String?, output: String) {
        chrVersionCommand = command?.trim()?.takeIf { it.isNotBlank() } ?: effectiveCommand() ?: ""
        chrVersionOutput = output.trim()
    }

    /** Whether [chrVersionOutput] exists and came from the command that would run now. */
    fun chrVersionInfoIsCurrent(): Boolean =
        chrVersionOutput.isNotBlank() && chrVersionCommand == (effectiveCommand() ?: "")

    /**
     * The maximal Rell version the Chromia CLI reported (its `rell version` line), or null when
     * no output is recorded, it carried no parseable version, or it came from a command other
     * than the one that would run now — stale info must not drive warnings.
     */
    fun reportedMaxRellVersion(): RellVersion? {
        if (!chrVersionInfoIsCurrent()) return null
        return RELL_VERSION_LINE.find(chrVersionOutput)?.groupValues?.get(1)?.let(RellVersion::parse)
    }

    internal fun effectiveCommand(): String? =
        chromiaCliCommand.trim().takeIf { it.isNotBlank() } ?: detectChromiaCliPath()

    companion object {
        val instance: RellPluginSettingsState
            get() = ApplicationManager.getApplication().getService(RellPluginSettingsState::class.java)

        /** The `rell version x.y.z` line of `chr --version` output. */
        private val RELL_VERSION_LINE =
            Regex("""^rell version\s+(\S+)""", setOf(RegexOption.MULTILINE, RegexOption.IGNORE_CASE))

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
            return (System.getenv("PATH") ?: return null)
                .splitToSequence(File.pathSeparator)
                .map { dir -> File(dir, name) }
                .find { it.canExecute() }
                ?.absolutePath
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
