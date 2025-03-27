package net.postchain.rellide.jetbrains.actions

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.LanguageServerManager
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import net.postchain.rellide.jetbrains.lsp4ij.RellServerApi

class RellInvalidateCacheAction : AnAction(
    "Rell: Invalidate Cache",
    "Invalidates and removes the Rell cache folder",
    null
) {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        try {
            runBlocking {
                val languageServerItem = LanguageServerManager.getInstance(project)
                    .getLanguageServer("rellLanguageServer")
                    .get()

                if (languageServerItem == null) {
                    project.notifyUser("Rell Language server is not running", "Error", NotificationType.ERROR)
                } else {
                    val rellServer = languageServerItem.server as RellServerApi
                    val invalidated = rellServer.invalidateCache().await()

                    if (invalidated) {
                        project.notifyUser(
                            "Cache invalidated",
                            "Rell LSP Info",
                            NotificationType.INFORMATION
                        )
                    } else {
                        project.notifyUser(
                            "Cache not be invalidated",
                            "Rell LSP Error",
                            NotificationType.WARNING
                        )
                    }
                }
            }
        } catch (e: Exception) {
            project.notifyUser(
                "Error invalidating cache: ${e.message}",
                "Rell LSP Error",
                NotificationType.ERROR
            )
        }
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = e.project != null
    }

    private fun Project.notifyUser(title: String, message: String, type: NotificationType) {
        ApplicationManager.getApplication().invokeLater {
            NotificationGroupManager.getInstance()
                .getNotificationGroup("Rell")
                .createNotification(title, message, type)
                .notify(this)
        }
    }

}