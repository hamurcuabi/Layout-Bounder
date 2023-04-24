package com.hamurcuabi.layoutbounder.services

import com.hamurcuabi.layoutbounder.model.CommandHistory
import com.intellij.openapi.components.service

class LogService {

    private val properties = service<PropertiesService>()

    var isLogVisible = properties.isLogVisible
        set(value) {
            field = value
            properties.isLogVisible = value
            logVisibilityListener?.invoke(value)
        }

    var logVisibilityListener: ((isVisible: Boolean) -> Unit)? = null
        set(value) {
            field = value
            value?.invoke(isLogVisible)
        }

    val commandHistory = CommandHistory()
}
