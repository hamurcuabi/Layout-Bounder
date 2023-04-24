package com.hamurcuabi.layoutbounder.actions

import com.hamurcuabi.layoutbounder.services.AdbService
import com.hamurcuabi.layoutbounder.utils.appCoroutineScope
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.DumbAware
import com.intellij.ui.AnimatedIcon
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RestartAdbAction : AnAction(), DumbAware {

    private val adbService by lazy { ApplicationManager.getApplication().getService(AdbService::class.java) }

    override fun actionPerformed(e: AnActionEvent) {
        adbService.restartAdb()

        val icon = e.presentation.icon
        e.presentation.icon = AnimatedIcon.Default()
        appCoroutineScope.launch {
            delay(1000)
            e.presentation.icon = icon
        }
    }
}
