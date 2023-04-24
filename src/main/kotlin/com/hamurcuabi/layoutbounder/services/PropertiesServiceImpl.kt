package com.hamurcuabi.layoutbounder.services

import com.hamurcuabi.layoutbounder.utils.ADB_DEFAULT_PORT
import com.hamurcuabi.layoutbounder.utils.findScrcpyExecInSystemPath
import com.hamurcuabi.layoutbounder.utils.hasAdbInSystemPath
import com.hamurcuabi.layoutbounder.utils.isValidAdbLocation
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.util.SystemInfo
import java.io.File

class PropertiesServiceImpl : PropertiesService {

    private val properties = PropertiesComponent.getInstance()

    override var isLogVisible: Boolean
        get() = properties.getBoolean(IS_LOG_VISIBLE_PROPERTY, false)
        set(value) {
            properties.setValue(IS_LOG_VISIBLE_PROPERTY, value)
        }

    override var isPreviouslyConnectedDevicesExpanded: Boolean
        get() = properties.getBoolean(IS_PREVIOUSLY_CONNECTED_DEVICES_EXPANDED, true)
        set(value) {
            properties.setValue(IS_PREVIOUSLY_CONNECTED_DEVICES_EXPANDED, value, true)
        }

    override var confirmDeviceRemoval: Boolean
        get() = properties.getBoolean(CONFIRM_DEVICE_REMOVAL, true)
        set(value) {
            properties.setValue(CONFIRM_DEVICE_REMOVAL, value, true)
        }

    override var useAdbFromPath: Boolean
        get() = properties.getBoolean(ADB_FROM_SYSTEM_PATH, false)
        set(value) {
            properties.setValue(ADB_FROM_SYSTEM_PATH, value)
            notifyAdbLocationListener()
        }

    override var adbLocation: String
        get() = properties.getValue(ADB_LOCATION_PROPERTY, defaultAdbLocation)
        set(value) {
            properties.setValue(ADB_LOCATION_PROPERTY, value)
            notifyAdbLocationListener()
        }

    override var adbPort: Int
        get() = properties.getInt(ADB_PORT, ADB_DEFAULT_PORT)
        set(value) {
            properties.setValue(ADB_PORT, value, ADB_DEFAULT_PORT)
        }

    override var scrcpyEnabled: Boolean
        get() = properties.getBoolean(SCRCPY_ENABLED, defaultScrcpyEnabled)
        set(value) {
            properties.setValue(SCRCPY_ENABLED, value, defaultScrcpyEnabled)
            notifyScrcpyEnabledListener()
        }

    override val defaultScrcpyEnabled: Boolean
        get() {
            return findScrcpyExecInSystemPath() != null
        }

    override var useScrcpyFromPath: Boolean
        get() = properties.getBoolean(SCRCPY_FROM_SYSTEM_PATH, true)
        set(value) {
            properties.setValue(SCRCPY_FROM_SYSTEM_PATH, value, true)
        }

    override var scrcpyLocation: String
        get() = properties.getValue(SCRCPY_LOCATION_PROPERTY, defaultScrcpyLocation)
        set(value) {
            properties.setValue(SCRCPY_LOCATION_PROPERTY, value)
        }

    override var scrcpyCmdFlags: String
        get() = properties.getValue(SCRCPY_CMD_FLAGS, "")
        set(value) {
            properties.setValue(SCRCPY_CMD_FLAGS, value)
        }

    override val defaultAdbLocation: String by lazy {
        val home = System.getProperty("user.home")
        val path = when {
            SystemInfo.isMac -> "$home/Library/Android/sdk/platform-tools"
            SystemInfo.isWindows -> "$home/AppData/Local/Android/Sdk/platform-tools"
            else -> "$home/Android/Sdk/platform-tools"
        }
        return@lazy File(path).absolutePath
    }

    override val defaultScrcpyLocation: String = ""

    override var adbLocationListener: ((isValid: Boolean) -> Unit)? = null
        set(value) {
            field = value
            notifyAdbLocationListener()
        }

    private fun notifyAdbLocationListener() {
        val isValid = when {
            useAdbFromPath -> hasAdbInSystemPath()
            else -> isValidAdbLocation(adbLocation)
        }
        adbLocationListener?.invoke(isValid)
    }

    override var scrcpyEnabledListener: ((isEnabled: Boolean) -> Unit)? = null
        set(value) {
            field = value
            notifyScrcpyEnabledListener()
        }

    private fun notifyScrcpyEnabledListener() {
        scrcpyEnabledListener?.invoke(scrcpyEnabled)
    }

    private companion object {
        private const val IS_LOG_VISIBLE_PROPERTY = "com.hamurcuabi.layoutbounder.IS_LOG_VISIBLE_PROPERTY"

        private const val IS_PREVIOUSLY_CONNECTED_DEVICES_EXPANDED =
            "com.hamurcuabi.layoutbounder.IS_PREVIOUSLY_CONNECTED_DEVICES_EXPANDED"

        private const val CONFIRM_DEVICE_REMOVAL = "com.hamurcuabi.layoutbounder.CONFIRM_DEVICE_REMOVAL"

        private const val ADB_FROM_SYSTEM_PATH = "com.hamurcuabi.layoutbounder.ADB_FROM_SYSTEM_PATH"
        private const val ADB_LOCATION_PROPERTY = "com.hamurcuabi.layoutbounder.ADB_LOCATION_PROPERTY"
        private const val ADB_PORT = "com.hamurcuabi.layoutbounder.ADB_PORT"

        private const val SCRCPY_ENABLED = "com.hamurcuabi.layoutbounder.SCRCPY_ENABLED"
        private const val SCRCPY_FROM_SYSTEM_PATH = "com.hamurcuabi.layoutbounder.SCRCPY_FROM_SYSTEM_PATH"
        private const val SCRCPY_LOCATION_PROPERTY = "com.hamurcuabi.layoutbounder.SCRCPY_LOCATION_PROPERTY"
        private const val SCRCPY_CMD_FLAGS = "com.hamurcuabi.layoutbounder.SCRCPY_CMD_FLAGS"
    }
}
