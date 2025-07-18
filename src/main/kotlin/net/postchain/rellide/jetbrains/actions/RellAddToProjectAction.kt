package net.postchain.rellide.jetbrains.actions


import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.ui.popup.PopupStep
import com.intellij.openapi.ui.popup.util.BaseListPopupStep
import com.redhat.devtools.lsp4ij.LanguageServerManager
import com.redhat.devtools.lsp4ij.ServerStatus
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import net.postchain.rellide.jetbrains.lsp4ij.AddToProjectParams
import net.postchain.rellide.jetbrains.lsp4ij.RellServerApi
import net.postchain.rellide.jetbrains.lsp4ij.TemplateOptions
import net.postchain.rellide.jetbrains.lsp4ij.getRellLanguageServerItem
import net.postchain.rellide.jetbrains.lsp4ij.getRellLanguageServerStatus
import net.postchain.rellide.jetbrains.util.normalizedUri
import java.io.File

data class FeatureOption(val id: String, val displayName: String)

class RellAddToProjectAction : AnAction(
        "Rell: Add To Project",
        "Adds feature support to the current project",
        null
) {
    companion object {
        private val featureOptions = listOf(
                FeatureOption(id = "dev_container", displayName = "Dev Container"),
        )
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        showSelectionPopup(project)
    }

    private fun showSelectionPopup(project: Project) {
        val popupStep = object : BaseListPopupStep<FeatureOption>("Select feature to add", featureOptions) {
            override fun getTextFor(value: FeatureOption): String = value.displayName
            override fun onChosen(selectedValue: FeatureOption, finalChoice: Boolean): PopupStep<*>? {
                if (finalChoice) {
                    executeSelectedAction(selectedValue, project)
                }
                return FINAL_CHOICE
            }
        }
        val popup = JBPopupFactory.getInstance().createListPopup(popupStep)
        popup.showCenteredInCurrentWindow(project)
    }

    private fun executeSelectedAction(selectedValue: FeatureOption, project: Project) {
        try {
            runBlocking {
                val languageServerItem = getRellLanguageServerItem(project)

                if (languageServerItem == null) {
                    project.notifyUser("Rell Language server is not running", "Error", NotificationType.ERROR)
                } else {
                    val rellServer = languageServerItem.server as RellServerApi
                    val targetDirUri = project.guessProjectDir()?.normalizedUri() ?: return@runBlocking
                    val options = TemplateOptions(
                            includeDevContainer = selectedValue.id == "dev_container",
                    )
                    rellServer.addToProject(AddToProjectParams(targetDirUri, options)).await()
                    project.notifyUser("${selectedValue.displayName} added to project", "Rell", NotificationType.INFORMATION)
                    refreshProjectRoot(project)
                }
            }
        } catch (e: Exception) {
            project.notifyUser("Error adding feature: ${e.message}", "Rell", NotificationType.ERROR)
        }
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        val project = e.project
        e.presentation.isEnabled = project != null && getRellLanguageServerStatus(project) == ServerStatus.started
    }

    fun refreshProjectRoot(project: Project) {
        ApplicationManager.getApplication().invokeLater {
            val projectRootManager = ProjectRootManager.getInstance(project)
            val contentRoots = projectRootManager.contentRoots

            ApplicationManager.getApplication().runWriteAction {
                contentRoots.forEach { root ->
                    root.refresh(false, true)
                }
            }
        }
    }
}
