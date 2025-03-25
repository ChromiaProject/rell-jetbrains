package net.postchain.rellide.jetbrains.actions

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.LanguageServerManager
import net.postchain.rellide.jetbrains.lsp4ij.RellServerApi

class RellInvalidateCacheAction : AnAction(
    "Invalidate Rell Cache",
    "Invalidates and removes the Rell cache folder",
    null
) {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        LanguageServerManager.getInstance(project)
            .getLanguageServer("rellLanguageServer")
            .thenAccept { languageServerItem ->
                if (languageServerItem == null) {
                    project.notifyUser("Rell Language server is not running", "Error", NotificationType.ERROR)
                }
                else {
                    val rellServer = languageServerItem.server as RellServerApi
                    val result = rellServer.invalidateCacheSafely()
                    val (title, message, type) = result.toNotification()
                    project.notifyUser(title, message, type)
                }
            }
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = e.project != null
    }

    sealed interface InvalidateCacheResult {
        data object Success : InvalidateCacheResult
        data object Failure : InvalidateCacheResult
        data class Error(val message: String) : InvalidateCacheResult
    }

    private fun Project.notifyUser(title: String, message: String, type: NotificationType) {
        ApplicationManager.getApplication().invokeLater {
            NotificationGroupManager.getInstance()
                .getNotificationGroup("Rell")
                .createNotification(title, message, type)
                .notify(this)
        }
    }

    private fun RellServerApi.invalidateCacheSafely(): InvalidateCacheResult = runCatching {
        if (invalidateCache().get())
            InvalidateCacheResult.Success
        else
            InvalidateCacheResult.Failure
    }.fold (
        onSuccess = { it },
        onFailure = { InvalidateCacheResult.Error(it.message ?: "Unknown LSP Error") }
    )

    private fun InvalidateCacheResult.toNotification(): Triple<String, String, NotificationType> = when (this) {
        is InvalidateCacheResult.Success ->
            Triple("Cache invalidated", "Rell LSP Info", NotificationType.INFORMATION)
        is InvalidateCacheResult.Failure ->
            Triple("Cache not be invalidated", "Rell LSP Error", NotificationType.WARNING)
        is InvalidateCacheResult.Error ->
            Triple("Error invalidating cache: $message", "Rell LSP Error", NotificationType.ERROR)
    }

}