package com.hamurcuabi.layoutbounder.settings

import com.hamurcuabi.layoutbounder.PluginBundle
import com.hamurcuabi.layoutbounder.services.PropertiesService
import com.hamurcuabi.layoutbounder.utils.*
import com.intellij.openapi.components.service
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.TextComponentAccessor
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.util.SystemInfo
import com.intellij.ui.HyperlinkLabel
import com.intellij.ui.JBColor
import com.intellij.ui.TitledSeparator
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.Insets
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.io.File
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class LayoutBounderConfigurable : Configurable {

    private val properties = service<PropertiesService>()

    private lateinit var adbPortField: JTextField
    private lateinit var adbSystemPathCheckbox: JBCheckBox
    private lateinit var adbLocationTitle: JBLabel
    private lateinit var adbLocationField: TextFieldWithBrowseButton
    private lateinit var adbStatusLabel: JBLabel
    private lateinit var defaultAdbLocationButton: HyperlinkLabel

    private lateinit var confirmDeviceRemovalCheckbox: JBCheckBox

    override fun getDisplayName(): String {
        return PluginBundle.message("settingsPageName")
    }

    override fun createComponent(): JComponent {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.PAGE_AXIS)

        panel.add(createAdbSettingsPanel())
        panel.add(Box.createRigidArea(Dimension(0, GROUP_VERTICAL_INSET)))
        panel.add(Box.createRigidArea(Dimension(0, GROUP_VERTICAL_INSET)))
        panel.add(createGeneralSettingsPanel())

        verifyAdbLocation()
        updateAdbLocationSettingsState()

        return panel(top = panel)
    }

    private fun createAdbPortField() = JBTextField(7).apply {
        document = MaxLengthNumberDocument(5)
        makeMonospaced()
        text = properties.adbPort.toString()
    }

    private fun createAdbSystemPathCheckbox() = JBCheckBox(PluginBundle.message("adbUseSystemPath")).apply {
        isSelected = properties.useAdbFromPath
        addItemListener {
            updateAdbLocationSettingsState()
        }
    }

    private fun createAdbLocationField() = TextFieldWithBrowseButton().apply {
        text = properties.adbLocation
        textField.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent) = verifyAdbLocation()
            override fun removeUpdate(e: DocumentEvent) = verifyAdbLocation()
            override fun changedUpdate(e: DocumentEvent) = verifyAdbLocation()
        })
        addBrowseFolderListener(
            null,
            null,
            null,
            executableChooserDescriptor(),
            ExecutablePathTextComponentAccessor()
        )
    }

    private fun createDefaultAdbLocationButton() =
        HyperlinkLabel(PluginBundle.message("defaultAdbLocationButton")).apply {
            addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent) {
                    adbLocationField.text = properties.defaultAdbLocation
                }
            })
        }

    private fun createAdbSettingsPanel(): JPanel {
        val panel = GridBagLayoutPanel()

        val separator = TitledSeparator(PluginBundle.message("adbSettingsTitle"))
        panel.add(
            separator,
            GridBagConstraints().apply {
                gridx = 0
                gridy = 0
                gridwidth = 3
                fill = GridBagConstraints.HORIZONTAL
                weightx = 1.0
            }
        )

        @Suppress("DialogTitleCapitalization")
        val adbPortTitle = JBLabel(PluginBundle.message("adbPortTitle"))
        panel.add(
            adbPortTitle,
            GridBagConstraints().apply {
                gridx = 0
                gridy = 1
                gridwidth = 1
                anchor = GridBagConstraints.LINE_START
                insets = Insets(COMPONENT_VERTICAL_INSET, GROUP_LEFT_INSET, 0, 8)
            }
        )

        adbPortField = createAdbPortField()
        panel.add(
            adbPortField,
            GridBagConstraints().apply {
                gridx = 1
                gridy = 1
                gridwidth = 1
                fill = GridBagConstraints.HORIZONTAL
                weightx = 1.0
                insets = Insets(COMPONENT_VERTICAL_INSET, 0, 0, 0)
            }
        )

        adbSystemPathCheckbox = createAdbSystemPathCheckbox()
        panel.add(
            adbSystemPathCheckbox,
            GridBagConstraints().apply {
                gridx = 0
                gridy = 2
                gridwidth = 3
                anchor = GridBagConstraints.LINE_START
                insets = Insets(GROUP_VERTICAL_INSET, GROUP_LEFT_INSET, 0, 0)
            }
        )

        adbLocationTitle = JBLabel(PluginBundle.message("adbPathTitle"))
        panel.add(
            adbLocationTitle,
            GridBagConstraints().apply {
                gridx = 0
                gridy = 3
                gridwidth = 1
                anchor = GridBagConstraints.LINE_START
                insets = Insets(COMPONENT_VERTICAL_INSET, GROUP_LEFT_INSET, 0, 8)
            }
        )

        adbLocationField = createAdbLocationField()
        panel.add(
            adbLocationField,
            GridBagConstraints().apply {
                gridx = 1
                gridy = 3
                gridwidth = 2
                fill = GridBagConstraints.HORIZONTAL
                weightx = 1.0
                insets = Insets(COMPONENT_VERTICAL_INSET, 0, 0, 0)
            }
        )

        adbStatusLabel = JBLabel()
        panel.add(
            adbStatusLabel,
            GridBagConstraints().apply {
                gridx = 1
                gridy = 4
                gridwidth = 1
                fill = GridBagConstraints.HORIZONTAL
                weightx = 1.0
                insets = Insets(COMPONENT_VERTICAL_INSET, 0, 0, 0)
            }
        )

        defaultAdbLocationButton = createDefaultAdbLocationButton()
        panel.add(
            defaultAdbLocationButton,
            GridBagConstraints().apply {
                gridx = 2
                gridy = 4
                gridwidth = 1
                insets = Insets(4, 0, 0, 0)
            }
        )

        return panel
    }

    private fun createGeneralSettingsPanel(): JPanel {
        val panel = GridBagLayoutPanel()

        val separator = TitledSeparator(PluginBundle.message("generalSettingsTitle"))
        panel.add(
            separator,
            GridBagConstraints().apply {
                gridx = 0
                gridy = 0
                fill = GridBagConstraints.HORIZONTAL
                weightx = 1.0
            }
        )

        confirmDeviceRemovalCheckbox = JBCheckBox(PluginBundle.message("confirmDeviceRemoval"))
        confirmDeviceRemovalCheckbox.isSelected = properties.confirmDeviceRemoval
        panel.add(
            confirmDeviceRemovalCheckbox,
            GridBagConstraints().apply {
                gridx = 0
                gridy = 1
                anchor = GridBagConstraints.LINE_START
                insets = Insets(GROUP_VERTICAL_INSET, GROUP_LEFT_INSET, 0, 0)
            }
        )

        return panel
    }

    override fun isModified(): Boolean {
        if (adbSystemPathCheckbox.isSelected != properties.useAdbFromPath) return true
        if (adbLocationField.text != properties.adbLocation) return true
        if ((adbPortField.text.toIntOrNull() ?: ADB_DEFAULT_PORT) != properties.adbPort) return true

        if (confirmDeviceRemovalCheckbox.isSelected != properties.confirmDeviceRemoval) return true

        return false
    }

    override fun apply() {
        properties.adbLocation = adbLocationField.text
        properties.useAdbFromPath = adbSystemPathCheckbox.isSelected
        properties.adbPort = adbPortField.text.toIntOrNull() ?: ADB_DEFAULT_PORT
        adbPortField.text = properties.adbPort.toString()

        properties.confirmDeviceRemoval = confirmDeviceRemovalCheckbox.isSelected
    }

    override fun reset() {
        adbSystemPathCheckbox.isSelected = properties.useAdbFromPath
        adbLocationField.text = properties.adbLocation
        adbPortField.text = properties.adbPort.toString()
        confirmDeviceRemovalCheckbox.isSelected = properties.confirmDeviceRemoval
    }

    private fun executableChooserDescriptor(): FileChooserDescriptor = when {
        SystemInfo.isMac -> FileChooserDescriptorFactory.createSingleFileOrFolderDescriptor()
        else -> FileChooserDescriptorFactory.createSingleFolderDescriptor()
    }

    private fun showAdbVerifiedMessage() {
        adbStatusLabel.apply {
            icon = Icons.OK
            text = VERIFIED_MESSAGE
            foreground = JBColor.foreground()
        }

        defaultAdbLocationButton.isVisible = false
    }

    private fun showAdbVerificationErrorMessage() {
        adbStatusLabel.apply {
            icon = Icons.ERROR
            text = ADB_VERIFICATION_ERROR_MESSAGE
            foreground = JBColor.RED
        }

        defaultAdbLocationButton.isVisible = adbStatusLabel.isVisible
    }

    private fun verifyAdbLocation() {
        val dir = adbLocationField.text
        if (isValidAdbLocation(dir)) {
            showAdbVerifiedMessage()
        } else {
            showAdbVerificationErrorMessage()
        }
    }

    private fun updateAdbLocationSettingsState() {
        val enabled = !adbSystemPathCheckbox.isSelected
        adbLocationTitle.isEnabled = enabled
        adbLocationField.isEnabled = enabled
        adbStatusLabel.isVisible = enabled
        defaultAdbLocationButton.isVisible = enabled && adbStatusLabel.icon == Icons.ERROR
    }

    private class ExecutablePathTextComponentAccessor(
        val onTextChanged: (() -> Unit)? = null
    ) : TextComponentAccessor<JTextField> {

        override fun getText(component: JTextField): String = component.text

        override fun setText(component: JTextField, text: String) {
            val file = File(text)
            val dirName = if (file.isFile) file.parent.orEmpty() else text
            component.text = dirName

            onTextChanged?.invoke()
        }
    }

    private companion object {
        private const val GROUP_VERTICAL_INSET = 10
        private const val GROUP_LEFT_INSET = 20
        private const val COMPONENT_VERTICAL_INSET = 4
        private val VERIFIED_MESSAGE = PluginBundle.message("adbPathVerifiedMessage")
        private val ADB_VERIFICATION_ERROR_MESSAGE = PluginBundle.message("adbPathVerificationErrorMessage")
    }
}
