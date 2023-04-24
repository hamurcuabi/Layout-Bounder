package com.hamurcuabi.layoutbounder.model

import com.hamurcuabi.layoutbounder.utils.startsWithAny

data class Address(
    val interfaceName: String,
    val ip: String
) {
    val isWifiNetwork: Boolean
        get() = interfaceName.startsWith("wlan", ignoreCase = true)

    val isMobileNetwork: Boolean
        get() = interfaceName.startsWithAny("rmnet", "ccmni", ignoreCase = true)

    val isHotspotNetwork: Boolean
        get() = interfaceName.startsWithAny("swlan", "ap", ignoreCase = true)
}
