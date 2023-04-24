package com.hamurcuabi.layoutbounder.actions

import com.hamurcuabi.layoutbounder.services.LogService
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.project.DumbAware

class ClearLogAction : AnAction(), DumbAware {

    private val logService by lazy { service<LogService>() }

    override fun actionPerformed(e: AnActionEvent) {
        logService.commandHistory.clear()
    }
}
