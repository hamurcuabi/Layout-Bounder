package com.hamurcuabi.layoutbounder.model

import com.hamurcuabi.layoutbounder.model.Device.ConnectionType.*

data class Device(
    val id: String,
    val serialNumber: String,
    val name: String,
    val address: Address?,
    val port: Int,
    val androidVersion: String,
    val apiLevel: String,
    val connectionType: ConnectionType,
    val isPinnedDevice: Boolean = false,
    var isConnected: Boolean = false,
    var hasLayoutBounds: Boolean = false,
) {
    val uniqueId: String = "$serialNumber-$id"
    val isUsbDevice = connectionType == USB
    val isWifiDevice = connectionType == WIFI

    enum class ConnectionType {
        USB, WIFI, NONE
    }
}
