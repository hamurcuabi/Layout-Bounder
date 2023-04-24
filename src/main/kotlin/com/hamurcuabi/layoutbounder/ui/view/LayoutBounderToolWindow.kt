package com.hamurcuabi.layoutbounder.ui.view

import com.hamurcuabi.layoutbounder.PluginBundle
import com.hamurcuabi.layoutbounder.model.LogEntry
import com.hamurcuabi.layoutbounder.ui.model.DeviceViewModel
import com.hamurcuabi.layoutbounder.ui.presenter.ToolWindowPresenter
import com.hamurcuabi.layoutbounder.utils.GridBagLayoutPanel
import com.hamurcuabi.layoutbounder.utils.Icons
import com.hamurcuabi.layoutbounder.utils.panel
import com.hamurcuabi.layoutbounder.utils.setFontSize
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DoNotAskOption
import com.intellij.openapi.ui.MessageDialogBuilder
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.openapi.wm.ex.ToolWindowManagerListener
import com.intellij.ui.HyperlinkLabel
import com.intellij.ui.JBColor
import com.intellij.ui.JBSplitter
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.components.BorderLayoutPanel
import java.awt.GridBagConstraints
import java.awt.Insets
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*

class LayoutBounderToolWindow(
    private val project: Project,
    private val toolWindow: ToolWindow
) : BorderLayoutPanel(), Disposable, ToolWindowView {

    private val presenter = ToolWindowPresenter()

    private val devicePanelListener = object : DevicePanel.Listener {

        override fun onConnectButtonClicked(device: DeviceViewModel) {
            presenter.onConnectButtonClicked(device)
        }

        override fun onDisconnectButtonClicked(device: DeviceViewModel) {
            presenter.onDisconnectButtonClicked(device)
        }

        override fun onRemoveDeviceClicked(device: DeviceViewModel) {
            presenter.onRemoveDeviceButtonClicked(device)
        }

        override fun onCopyDeviceIdClicked(device: DeviceViewModel) {
            presenter.onCopyDeviceIdClicked(device)
        }

        override fun onCopyDeviceAddressClicked(device: DeviceViewModel) {
            presenter.onCopyDeviceAddressClicked(device)
        }
    }

    private val splitter = JBSplitter(true, "AdbWifi.ShellPaneProportion", DEFAULT_PANEL_PROPORTION)
    private val deviceListPanel = DeviceListPanel(devicePanelListener)
    private val logPanel = LogPanel()
    private val topPanel = JBScrollPane().apply {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.PAGE_AXIS)
        panel.add(deviceListPanel)
        this.setViewportView(panel)
    }
    private val bottomPanel: JComponent
    private val emptyMessageLabel = JBLabel().apply {
        text = PluginBundle.message("deviceListEmptyMessage")
        icon = Icons.DEVICE_LINEUP
        horizontalAlignment = SwingConstants.CENTER
        horizontalTextPosition = SwingConstants.CENTER
        verticalTextPosition = SwingConstants.BOTTOM
        setFontSize(16f)
        background = JBColor.background()
        foreground = JBColor.gray
        isOpaque = true
        border = BorderFactory.createLineBorder(JBColor.border())
    }
    private val errorMessagePanel = GridBagLayoutPanel().apply {
        background = JBColor.background()
        border = BorderFactory.createLineBorder(JBColor.border())

        val label = JBLabel().apply {
            @Suppress("DialogTitleCapitalization")
            text = PluginBundle.message("adbPathVerificationErrorMessage", location)
            icon = Icons.DEVICE_WARNING
            horizontalAlignment = SwingConstants.CENTER
            horizontalTextPosition = SwingConstants.CENTER
            verticalTextPosition = SwingConstants.BOTTOM
            setFontSize(16f)
            foreground = JBColor.gray
        }
        add(
            label,
            GridBagConstraints().apply {
                gridx = 0
                gridy = 0
            }
        )

        val settingsButton = HyperlinkLabel(PluginBundle.message("goToSettingsButton")).apply {
            setFontSize(16f)
            addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent) {
                    ShowSettingsUtil.getInstance().showSettingsDialog(
                        null,
                        PluginBundle.message("settingsPageName")
                    )
                }
            })
        }
        add(
            settingsButton,
            GridBagConstraints().apply {
                gridx = 0
                gridy = 1
                insets = Insets(20, 0, 20, 0)
            }
        )
    }

    init {
        val actionManager = ActionManager.getInstance()
        val toolbarActionGroup = actionManager.getAction("AdbWifi.ToolbarActions") as DefaultActionGroup
        val toolbar = actionManager.createActionToolbar(
            ActionPlaces.TOOLWINDOW_TITLE,
            toolbarActionGroup,
            true
        )
        toolbar.targetComponent = this
        addToTop(toolbar.component)
        addToCenter(splitter)

        val logToolbarActionGroup = actionManager.getAction("AdbWifi.LogToolbarActions") as DefaultActionGroup
        val logToolbar = actionManager.createActionToolbar(
            ActionPlaces.TOOLWINDOW_CONTENT,
            logToolbarActionGroup,
            false
        )
        bottomPanel = panel(center = JBScrollPane(logPanel), left = logToolbar.component)

        splitter.firstComponent = topPanel

        project.messageBus
            .connect(this)
            .subscribe(
                ToolWindowManagerListener.TOPIC,
                object : ToolWindowManagerListener {
                    override fun stateChanged(toolWindowManager: ToolWindowManager) {
                        if (toolWindow.isVisible) {
                            presenter.onViewOpen()
                        } else {
                            presenter.onViewClosed()
                        }
                    }
                }
            )

        presenter.attach(this)
    }

    override fun dispose() {
        presenter.detach()
    }

    override fun showDevices(devices: List<DeviceViewModel>) {
        splitter.firstComponent = topPanel
        deviceListPanel.devices = devices
    }

    override fun showEmptyMessage() {
        splitter.firstComponent = emptyMessageLabel
    }

    override fun showInvalidAdbLocationError() {
        splitter.firstComponent = errorMessagePanel
    }

    override fun openLog() {
        splitter.secondComponent = bottomPanel
    }

    override fun closeLog() {
        splitter.secondComponent = null
    }

    override fun setLogEntries(entries: List<LogEntry>) {
        logPanel.setLogEntries(entries)
    }

    override fun showRemoveDeviceConfirmation(device: DeviceViewModel) {
        val doNotAskAgain = object : DoNotAskOption.Adapter() {
            override fun rememberChoice(isSelected: Boolean, exitCode: Int) {
                if (exitCode == Messages.OK) {
                    presenter.onRemoveDeviceConfirmed(doNotAskAgain = isSelected)
                }
            }

            override fun getDoNotShowMessage(): String {
                return PluginBundle.message("doNotAskAgain")
            }
        }
        MessageDialogBuilder.yesNo(title = device.titleText, message = PluginBundle.message("removeDeviceConfirmation"))
            .yesText(PluginBundle.message("removeButton"))
            .noText(PluginBundle.message("cancelButton"))
            .doNotAsk(doNotAskAgain)
            .ask(project)
    }

    private companion object {
        private const val DEFAULT_PANEL_PROPORTION = 0.6f
    }
}
