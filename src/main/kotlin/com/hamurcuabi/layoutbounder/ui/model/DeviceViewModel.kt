package com.hamurcuabi.layoutbounder.ui.model

import com.hamurcuabi.layoutbounder.model.Device
import com.hamurcuabi.layoutbounder.model.Device.ConnectionType.*
import com.hamurcuabi.layoutbounder.utils.Icons
import javax.swing.Icon

data class DeviceViewModel(
    val device: Device,
    val titleText: String,
    val subtitleText: String,
    val subtitleIcon: Icon?,
    val icon: Icon,
    val hasAddress: Boolean,
    val buttonType: ButtonType,
    var isShareScreenButtonVisible: Boolean,
    val isRemoveButtonVisible: Boolean,
    var isInProgress: Boolean = false,
    var hasLayoutBounds: Boolean = false
) {
    val id: String
        get() = device.id

    val serialNumber: String
        get() = device.serialNumber

    val address: String?
        get() = device.address?.ip

    val uniqueId: String
        get() = device.uniqueId

    enum class ButtonType {
        CONNECT, CONNECT_DISABLED, DISCONNECT
    }

    companion object {

        fun Device.toViewModel(): DeviceViewModel {
            val device = this
            return DeviceViewModel(
                device = device,
                titleText = device.name,
                subtitleText = device.subtitleText(),
                subtitleIcon = device.addressIcon(),
                icon = device.icon(),
                hasAddress = device.hasAddress(),
                buttonType = device.buttonType(),
                isShareScreenButtonVisible = false,
                isRemoveButtonVisible = false,
                hasLayoutBounds = device.hasLayoutBounds
            )
        }

        private fun Device.subtitleText() = buildString {
            val device = this@subtitleText
            append("<html>")
            append("Android ${device.androidVersion} (API ${device.apiLevel}) -")
            if (device.address != null) {
                append(" <code>${device.address.ip}:${device.port}</code>")
            }
            append("</html>")
        }

        private fun Device.icon(): Icon = when (connectionType) {
            USB -> Icons.USB
            WIFI -> Icons.WIFI
            NONE -> Icons.NO_USB
        }

        private fun Device.addressIcon(): Icon? {
            address ?: return Icons.NO_WIFI
            if (connectionType != USB) return null
            return when {
                address.isWifiNetwork -> Icons.WIFI_NETWORK
                address.isMobileNetwork -> Icons.MOBILE_NETWORK
                address.isHotspotNetwork -> Icons.HOTSPOT_NETWORK
                else -> null
            }
        }

        private fun Device.hasAddress() = this.address != null

        private fun Device.buttonType(): ButtonType {
            val device = this
            return if (device.hasLayoutBounds) {
                ButtonType.DISCONNECT
            } else {
                ButtonType.CONNECT
            }
        }
    }
}
