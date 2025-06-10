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

    /**
     * Discover all Chromia projects in the workspace
     */
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
    
    /**
     * Recursively search for Rell projects
     */
    private fun searchForProjects(dir: File, projects: MutableList<ChromiaProject>, currentDepth: Int, maxDepth: Int) {
        if (currentDepth >= maxDepth || !dir.isDirectory) return
        
        val subdirs = dir.listFiles { file -> 
            file.isDirectory && !file.name.startsWith(".") && !isIgnoredDirectory(file.name)
        } ?: return
        
        for (subdir in subdirs) {
            val foundIndicator = indicators.find { indicator ->
                File(subdir, indicator).exists()
            }
            
            if (foundIndicator != null || hasRellFiles(subdir)) {
                val projectName = if (subdir.name == "src" && subdir.parentFile != null) {
                    // If the project is in a src directory, use parent directory name
                    subdir.parentFile.name
                } else {
                    subdir.name
                }
                
                projects.add(ChromiaProject(
                    name = projectName,
                    path = subdir.absolutePath,
                    configFile = foundIndicator,
                    isMainProject = false
                ))
            } else {
                // Continue searching in subdirectories
                searchForProjects(subdir, projects, currentDepth + 1, maxDepth)
            }
        }
    }
    
    /**
     * Check if directory contains .rell files
     */
    private fun hasRellFiles(dir: File): Boolean {
        val rellFiles = dir.listFiles { file -> 
            file.isFile && file.name.endsWith(".rell")
        }
        
        if (rellFiles?.isNotEmpty() == true) {
            return true
        }
        
        // Check in src subdirectory
        val srcDir = File(dir, "src")
        if (srcDir.exists() && srcDir.isDirectory) {
            val srcRellFiles = srcDir.listFiles { file ->
                file.isFile && file.name.endsWith(".rell")
            }
            return srcRellFiles?.isNotEmpty() == true
        }
        
        return false
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