package net.postchain.rellide.jetbrains.actions

import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.progress.runBlockingCancellable
import net.postchain.rellide.jetbrains.lsp.getRellLspClient
import net.postchain.rellide.jetbrains.lsp.rellRequest

class RellInvalidateCacheAction : AnAction(
    "Rell: Invalidate Cache",
    "Invalidates and removes the Rell cache folder",
    null
) {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        try {
            runBlockingCancellable {
                val client = getRellLspClient(project)

                if (client == null) {
                    project.notifyUser("Rell Language server is not running", "Error", NotificationType.ERROR)
                } else {
                    val invalidated = client.rellRequest { it.invalidateCache() } == true

                    if (invalidated) {
                        project.notifyUser("Cache invalidated", "Rell LSP Info", NotificationType.INFORMATION)
                    } else {
                        project.notifyUser("Cache not be invalidated", "Rell LSP Error", NotificationType.WARNING)
                    }
                }
            }
        } catch (e: Exception) {
            project.notifyUser("Error invalidating cache: ${e.message}", "Rell LSP Error", NotificationType.ERROR)
        }
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = e.project != null
    }
}
