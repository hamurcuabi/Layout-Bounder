package com.hamurcuabi.layoutbounder.ui.view

import com.hamurcuabi.layoutbounder.model.LogEntry
import com.hamurcuabi.layoutbounder.ui.model.DeviceViewModel

interface ToolWindowView {

    fun showDevices(devices: List<DeviceViewModel>)

    fun showEmptyMessage()

    fun showInvalidAdbLocationError()

    fun openLog()

    fun closeLog()

    fun setLogEntries(entries: List<LogEntry>)

    fun showRemoveDeviceConfirmation(device: DeviceViewModel)
}
