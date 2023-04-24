package com.hamurcuabi.layoutbounder.actions

import com.hamurcuabi.layoutbounder.PluginBundle
import com.intellij.notification.Notification
import com.intellij.notification.NotificationAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.options.ShowSettingsUtil

class OpenSettingsNotificationAction : NotificationAction(PluginBundle.message("goToSettingsButton")) {

    override fun actionPerformed(e: AnActionEvent, notification: Notification) {
        ShowSettingsUtil.getInstance().showSettingsDialog(null, PluginBundle.message("settingsPageName"))
    }
}
