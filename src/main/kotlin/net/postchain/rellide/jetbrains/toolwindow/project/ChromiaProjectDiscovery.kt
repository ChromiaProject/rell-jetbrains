package net.postchain.rellide.jetbrains.toolwindow.project

import com.intellij.openapi.project.Project
import java.io.File

/**
 * Service for discovering Chromia projects within a workspace.
 * Identifies subprojects that have Chromia configuration files.
 */
object ChromiaProjectDiscovery {
    private val indicators = listOf("chromia.yml", "chromia.yaml")

    data class ChromiaProject(
        val name: String,
        val path: String,
        val configFile: String? = null,
        val isMainProject: Boolean = false
    )

    fun discoverProjects(project: Project): List<ChromiaProject> {
        val projects = mutableListOf<ChromiaProject>()
        val basePath = project.basePath ?: return projects

        projects.addAll(discoverSubprojects(basePath))
        
        return projects.sortedBy { it.name }
    }

    private fun discoverSubprojects(basePath: String): List<ChromiaProject> {
        val projects = mutableListOf<ChromiaProject>()
        val baseDir = File(basePath)

        searchForProjects(baseDir, projects, currentDepth = 0, maxDepth = 10)

        return projects
    }

    private fun searchForProjects(dir: File, projects: MutableList<ChromiaProject>, currentDepth: Int, maxDepth: Int) {
        if (currentDepth >= maxDepth || !dir.isDirectory) return

        findConfigFile(dir)?.let { configFile ->
            projects.add(ChromiaProject(
                    name = dir.name,
                    path = dir.absolutePath,
                    configFile = configFile,
                    isMainProject = false
            ))
        }

        val subdirs = dir.listFiles { file -> 
            file.isDirectory && !file.name.startsWith(".") && !isIgnoredDirectory(file.name)
        } ?: return
        
        for (subdir in subdirs) {
            findConfigFile(subdir)?.let { configFile ->
                projects.add(ChromiaProject(
                        name = subdir.name,
                        path = subdir.absolutePath,
                        configFile = configFile,
                        isMainProject = false
                ))
            } ?: run {
                searchForProjects(subdir, projects, currentDepth + 1, maxDepth)
            }
        }
    }

    private fun findConfigFile(dir: File): String? {
        for (indicator in indicators) {
            val potentialConfigFile = File(dir, indicator)
            if (potentialConfigFile.exists() && potentialConfigFile.isFile) {
                return potentialConfigFile.absolutePath
            }
        }
        return null
    }

    private fun isIgnoredDirectory(name: String): Boolean {
        val ignoredDirs = setOf(
            "node_modules", "target", "build", "dist", "out",
            ".git", ".svn", ".hg", ".idea", ".vscode",
            "venv", "__pycache__", ".gradle", ".maven"
        )
        return ignoredDirs.contains(name)
    }
}