package net.postchain.rellide.jetbrains.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import net.postchain.rellide.jetbrains.RellBundle

@Service(Service.Level.PROJECT)
class RellProjectService(project: Project) {

    init {
        thisLogger().info(RellBundle.message("projectService", project.name))
        thisLogger().warn("Don't forget to remove all non-needed sample code files with their corresponding registration entries in `plugin.xml`.")
    }

    fun getRandomNumber() = (1..100).random()
}
