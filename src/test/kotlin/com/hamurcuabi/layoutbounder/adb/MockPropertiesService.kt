package com.hamurcuabi.layoutbounder.adb

import com.hamurcuabi.layoutbounder.services.PropertiesService
import com.hamurcuabi.layoutbounder.utils.ADB_DEFAULT_PORT

@SuppressWarnings("LongParameterList")
class MockPropertiesService(
    override var isLogVisible: Boolean = false,
    override var isPreviouslyConnectedDevicesExpanded: Boolean = true,
    override var confirmDeviceRemoval: Boolean = false,
    override var useAdbFromPath: Boolean = false,
    override var adbLocation: String = "/bin",
    override var defaultAdbLocation: String = "/bin",
    override var adbPort: Int = ADB_DEFAULT_PORT,
    override var adbLocationListener: ((isValid: Boolean) -> Unit)? = null,
    override var scrcpyEnabled: Boolean = true,
    override val defaultScrcpyEnabled: Boolean = true,
    override var useScrcpyFromPath: Boolean = true,
    override var scrcpyLocation: String = "/bin",
    override val defaultScrcpyLocation: String = "",
    override var scrcpyCmdFlags: String = "",
    override var scrcpyEnabledListener: ((isEnabled: Boolean) -> Unit)? = null
) : PropertiesService
