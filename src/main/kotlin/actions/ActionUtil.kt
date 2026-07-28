package net.postchain.rellide.jetbrains.actions

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project

fun Project.notifyUser(title: String, message: String, type: NotificationType) {
    ApplicationManager.getApplication().invokeLater {
        NotificationGroupManager.getInstance()
                .getNotificationGroup("Rell")
                .createNotification(title, message, type)
                .notify(this)
    }
}
