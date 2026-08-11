package net.postchain.rellide.jetbrains.toolwindow.project

import com.intellij.openapi.project.Project
import net.postchain.rell.toolbox.chromia.ChromiaModelProvider
import net.postchain.rellide.jetbrains.chromia.ChromiaActiveSettings
import net.postchain.rellide.jetbrains.chromia.ChromiaSettingsFiles
import net.postchain.rellide.jetbrains.chromia.RellVersion
import java.io.File
import java.io.IOException

/**
 * Service for discovering Chromia projects within a workspace: directories holding at least one
 * Chromia settings file — `chromia.yml`, or any `*.yml` that parses with the toolchain parser and
 * declares a `blockchains` section (the same qualification the version resolver applies). Each
 * project also carries which settings file is active, per [ChromiaActiveSettings] with the shared
 * default rule.
 */
object ChromiaProjectDiscovery {

    data class ChromiaProject(
        val name: String,
        val path: String,
        /** Absolute path of the active settings file. */
        val configFile: String? = null,
        /** All settings file names in the project directory, sorted. */
        val settingsFiles: List<String> = emptyList(),
        /** The active settings file name — chosen by the user or by the default rule. */
        val activeSettingsFile: String? = null,
        /** `compile.rellVersion` declared in the active settings file, null when absent or malformed. */
        val activeDeclaredVersion: RellVersion? = null,
    )

    fun discoverProjects(project: Project): List<ChromiaProject> {
        val basePath = project.basePath ?: return emptyList()
        return discoverProjects(basePath, ChromiaActiveSettings.getInstance(project))
    }

    internal fun discoverProjects(basePath: String, activeSettings: ChromiaActiveSettings): List<ChromiaProject> {
        val projects = mutableListOf<ChromiaProject>()
        searchForProjects(File(basePath), activeSettings, projects, currentDepth = 0, maxDepth = 10)
        return projects.sortedBy { it.name }
    }

    private fun searchForProjects(
        dir: File,
        activeSettings: ChromiaActiveSettings,
        projects: MutableList<ChromiaProject>,
        currentDepth: Int,
        maxDepth: Int,
    ) {
        if (currentDepth >= maxDepth || !dir.isDirectory) return

        val settingsFiles = findSettingsFiles(dir)

        if (settingsFiles.isNotEmpty()) {
            val active = activeSettingsFile(dir, settingsFiles, activeSettings)

            projects += ChromiaProject(
                name = dir.name,
                path = dir.absolutePath,
                configFile = File(dir, active).absolutePath,
                settingsFiles = settingsFiles,
                activeSettingsFile = active,
                activeDeclaredVersion = declaredVersionIn(File(dir, active)),
            )
        }

        val subdirs = dir.listFiles { file ->
            file.isDirectory && !file.name.startsWith(".") && !isIgnoredDirectory(file.name)
        } ?: return

        for (subdir in subdirs) {
            searchForProjects(subdir, activeSettings, projects, currentDepth + 1, maxDepth)
        }
    }

    private fun findSettingsFiles(dir: File): List<String> {
        val ymls = dir.listFiles { file -> file.isFile && ChromiaSettingsFiles.isYmlName(file.name) }
            ?: return emptyList()
        return ymls.filter { isSettingsFile(it) }.map { it.name }.sortedBy { it.lowercase() }
    }

    private fun isSettingsFile(file: File): Boolean {
        if (ChromiaSettingsFiles.isDefaultName(file.name)) return true
        val text = try {
            file.readText()
        } catch (_: IOException) {
            return false
        }
        if (!ChromiaSettingsFiles.TOP_LEVEL_BLOCKCHAINS.containsMatchIn(text)) return false
        // Extract-and-discard (see RellVersionResolver.parseWithToolchain): only null-check the
        // model, never retain, compare, or log it.
        return ChromiaModelProvider.loadChromiaModelFromFile(file.toPath()) != null
    }

    private fun activeSettingsFile(
        dir: File,
        settingsFiles: List<String>,
        activeSettings: ChromiaActiveSettings,
    ): String {
        // ChromiaActiveSettings normalizes the key, so the java.io.File path used here and the
        // VirtualFile path the banners use address the same entry on every platform.
        val chosen = activeSettings.activeFileName(dir.absolutePath)
            ?.let { selected -> settingsFiles.firstOrNull { it.equals(selected, ignoreCase = true) } }
        return chosen ?: ChromiaSettingsFiles.defaultChoice(settingsFiles) { declaredVersionIn(File(dir, it)) }
    }

    private fun declaredVersionIn(file: File): RellVersion? =
        ChromiaModelProvider.loadChromiaModelFromFile(file.toPath())?.compile?.rellVersion?.let(RellVersion::parse)

    private fun isIgnoredDirectory(name: String): Boolean {
        val ignoredDirs = setOf(
            "node_modules", "target", "build", "dist", "out",
            ".git", ".svn", ".hg", ".idea", ".vscode",
            "venv", "__pycache__", ".gradle", ".maven"
        )
        return ignoredDirs.contains(name)
    }
}
