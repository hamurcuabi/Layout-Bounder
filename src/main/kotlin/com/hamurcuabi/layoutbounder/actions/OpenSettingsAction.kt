package com.hamurcuabi.layoutbounder.actions

import com.hamurcuabi.layoutbounder.PluginBundle
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.DumbAware

class OpenSettingsAction : AnAction(), DumbAware {

    override fun actionPerformed(e: AnActionEvent) {
        ShowSettingsUtil.getInstance().showSettingsDialog(null, PluginBundle.message("settingsPageName"))
    }
}
