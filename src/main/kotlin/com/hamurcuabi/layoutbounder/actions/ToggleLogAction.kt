package com.hamurcuabi.layoutbounder.actions

import com.hamurcuabi.layoutbounder.services.LogService
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.ToggleAction
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.DumbAware

class ToggleLogAction : ToggleAction(), DumbAware {

    private val service by lazy { ApplicationManager.getApplication().getService(LogService::class.java) }

    override fun isSelected(e: AnActionEvent) = service.isLogVisible

    override fun setSelected(e: AnActionEvent, state: Boolean) {
        service.isLogVisible = state
    }
}
