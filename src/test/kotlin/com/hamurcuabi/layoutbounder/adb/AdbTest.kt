package com.hamurcuabi.layoutbounder.adb

import com.hamurcuabi.layoutbounder.model.Address
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class AdbTest {

    private val propertiesService = MockPropertiesService()

    @Test
    fun `test USB device with multiple IP addresses`() {
        val commandExecutor = object : MockCommandExecutor(propertiesService.adbLocation) {
            override fun mockOutput(command: String): String = when (command) {
                "$adb devices" -> {
                    """
                    List of devices attached
                    R28M51Y8E0H	device
                    """.trimIndent()
                }

                "$adb -s R28M51Y8E0H shell getprop ro.serialno" -> "R28M51Y8E0H"
                "$adb -s R28M51Y8E0H shell getprop ro.product.model" -> "SM-G9700"
                "$adb -s R28M51Y8E0H shell getprop ro.product.manufacturer" -> "samsung"
                "$adb -s R28M51Y8E0H shell getprop ro.build.version.release" -> "10"
                "$adb -s R28M51Y8E0H shell getprop ro.build.version.sdk" -> "29"
                "$adb -s R28M51Y8E0H shell ip route" -> {
                    """
                    100.106.57.0/29 dev rmnet_data0 proto kernel scope link src 100.106.57.3
                    192.168.1.0/24 dev wlan0 proto kernel scope link src 192.168.1.188
                    192.168.43.0/24 dev swlan0 proto kernel scope link src 192.168.43.1
                    """.trimIndent()
                }

                else -> throw NotImplementedError("Unknown command: '$command'")
            }
        }

        val devices = Adb(commandExecutor, propertiesService).devices()

        assertThat(devices).hasSize(1)
    }

    @Test
    fun `test Wi-Fi device with multiple IP addresses`() {
        val commandExecutor = object : MockCommandExecutor(propertiesService.adbLocation) {
            override fun mockOutput(command: String): String = when (command) {
                "$adb devices" -> {
                    """
                    List of devices attached
                    192.168.1.188:5555	device
                    """.trimIndent()
                }

                "$adb -s 192.168.1.188:5555 shell getprop ro.serialno" -> "192.168.1.188:5555"
                "$adb -s 192.168.1.188:5555 shell getprop ro.product.model" -> "SM-G9700"
                "$adb -s 192.168.1.188:5555 shell getprop ro.product.manufacturer" -> "samsung"
                "$adb -s 192.168.1.188:5555 shell getprop ro.build.version.release" -> "10"
                "$adb -s 192.168.1.188:5555 shell getprop ro.build.version.sdk" -> "29"
                "$adb -s 192.168.1.188:5555 shell ip route" -> {
                    """
                    100.106.57.0/29 dev rmnet_data0 proto kernel scope link src 100.106.57.3
                    192.168.1.0/24 dev wlan0 proto kernel scope link src 192.168.1.188
                    192.168.43.0/24 dev swlan0 proto kernel scope link src 192.168.43.1
                    """.trimIndent()
                }

                else -> throw NotImplementedError("Unknown command: '$command'")
            }
        }

        val devices = Adb(commandExecutor, propertiesService).devices()

        assertThat(devices).hasSize(1)

        assertThat(devices[0].address).isEqualTo(Address("wlan0", "192.168.1.188"))
    }

    @Test
    fun `test USB device with no network`() {
        val commandExecutor = object : MockCommandExecutor(propertiesService.adbLocation) {
            override fun mockOutput(command: String): String = when (command) {
                "$adb devices" -> {
                    """
                    List of devices attached
                    R28M51Y8E0H	device
                    """.trimIndent()
                }

                "$adb -s R28M51Y8E0H shell getprop ro.serialno" -> "R28M51Y8E0H"
                "$adb -s R28M51Y8E0H shell getprop ro.product.model" -> "SM-G9700"
                "$adb -s R28M51Y8E0H shell getprop ro.product.manufacturer" -> "samsung"
                "$adb -s R28M51Y8E0H shell getprop ro.build.version.release" -> "10"
                "$adb -s R28M51Y8E0H shell getprop ro.build.version.sdk" -> "29"
                "$adb -s R28M51Y8E0H shell ip route" -> ""
                else -> throw NotImplementedError("Unknown command: '$command'")
            }
        }

        val devices = Adb(commandExecutor, propertiesService).devices()

        assertThat(devices).hasSize(1)

        assertThat(devices[0].address).isNull()
    }

    @Test
    fun `test port address of Wi-Fi device`() {
        val commandExecutor = object : MockCommandExecutor(propertiesService.adbLocation) {
            override fun mockOutput(command: String): String = when (command) {
                "$adb devices" -> {
                    """
                    List of devices attached
                    192.168.1.188:1234	device
                    """.trimIndent()
                }

                "$adb -s 192.168.1.188:1234 shell getprop ro.serialno" -> "192.168.1.188:5555"
                "$adb -s 192.168.1.188:1234 shell getprop ro.product.model" -> "SM-G9700"
                "$adb -s 192.168.1.188:1234 shell getprop ro.product.manufacturer" -> "samsung"
                "$adb -s 192.168.1.188:1234 shell getprop ro.build.version.release" -> "10"
                "$adb -s 192.168.1.188:1234 shell getprop ro.build.version.sdk" -> "29"
                "$adb -s 192.168.1.188:1234 shell ip route" -> {
                    "192.168.1.0/24 dev wlan0 proto kernel scope link src 192.168.1.188"
                }

                else -> throw NotImplementedError("Unknown command: '$command'")
            }
        }

        val devices = Adb(commandExecutor, propertiesService).devices()

        assertThat(devices).hasSize(1)

        assertThat(devices[0].address).isEqualTo(Address("wlan0", "192.168.1.188"))
        assertThat(devices[0].port).isEqualTo(1234)
    }
}
