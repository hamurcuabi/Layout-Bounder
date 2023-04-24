package com.hamurcuabi.layoutbounder.ui.view

import com.hamurcuabi.layoutbounder.PluginBundle
import com.intellij.ide.plugins.newui.InstallButton

class Button(text: String, fill: Boolean) : InstallButton(fill) {

    init {
        this.text = text
    }

    override fun setTextAndSize() {
        /* no-op */
    }

    companion object {
        fun connectButton(isEnabled: Boolean = true): Button {
            return Button(PluginBundle.message("connectButton"), false).apply {
                this.isEnabled = isEnabled
            }
        }

        fun disconnectButton(isEnabled: Boolean = true): Button {
            return Button(PluginBundle.message("disconnectButton"), true).apply {
                this.isEnabled = isEnabled
            }
        }
    }
}
