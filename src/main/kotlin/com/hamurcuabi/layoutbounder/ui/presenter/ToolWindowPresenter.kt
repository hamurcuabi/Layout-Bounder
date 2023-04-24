package com.hamurcuabi.layoutbounder.ui.presenter

import com.hamurcuabi.layoutbounder.model.CommandHistory
import com.hamurcuabi.layoutbounder.model.Device
import com.hamurcuabi.layoutbounder.model.LogEntry
import com.hamurcuabi.layoutbounder.services.AdbService
import com.hamurcuabi.layoutbounder.services.LogService
import com.hamurcuabi.layoutbounder.services.PropertiesService
import com.hamurcuabi.layoutbounder.ui.model.DeviceViewModel
import com.hamurcuabi.layoutbounder.ui.model.DeviceViewModel.Companion.toViewModel
import com.hamurcuabi.layoutbounder.ui.view.ToolWindowView
import com.hamurcuabi.layoutbounder.utils.BasePresenter
import com.hamurcuabi.layoutbounder.utils.copyToClipboard
import com.intellij.openapi.components.service
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ToolWindowPresenter : BasePresenter<ToolWindowView>() {

    private val adbService by lazy { service<AdbService>() }
    private val logService by lazy { service<LogService>() }
    private val propertiesService by lazy { service<PropertiesService>() }

    private var isViewOpen: Boolean = false
    private var isAdbValid: Boolean = true
    private var devices: List<DeviceViewModel> = emptyList()

    private var connectingDevices = mutableSetOf<Pair<String/*Device's unique ID*/, String/*IP address*/>>()

    override fun attach(view: ToolWindowView) {
        super.attach(view)
        view.showEmptyMessage()
        subscribeToDeviceList()
        subscribeToLogEvents()
        subscribeToAdbLocationChanges()
        subscribeToScrcpyEnabledState()
    }

    override fun detach() {
        unsubscribeFromDeviceList()
        unsubscribeFromLogEvents()
        unsubscribeFromAdbLocationChanges()
        unsubscribeFromScrcpyEnabledState()
        super.detach()
    }

    fun onViewOpen() {
        isViewOpen = true
        if (isAdbValid) {
            subscribeToDeviceList()
        }
    }

    fun onViewClosed() {
        isViewOpen = false
        unsubscribeFromDeviceList()
    }

    fun onConnectButtonClicked(device: DeviceViewModel) {
        launch(Main) {
            val list = adbService.devices().also {
                it.firstOrNull {
                    it.id == device.id
                }?.apply {
                    hasLayoutBounds = device.hasLayoutBounds.not()
                }
            }

            val hasLayoutChanges = list.firstOrNull {
                it.id == device.id
            }?.hasLayoutBounds

            withContext(IO) {
                adbService.toggleLayoutBound(
                    state = hasLayoutChanges.toString(),
                    deviceId = device.id
                )
            }
            onDevicesUpdated(list)
        }
    }

    fun onDisconnectButtonClicked(device: DeviceViewModel) {
        onConnectButtonClicked(device)
    }

    fun onRemoveDeviceButtonClicked(device: DeviceViewModel) {
        if (propertiesService.confirmDeviceRemoval) {
            view?.showRemoveDeviceConfirmation(device)
        }
    }

    fun onRemoveDeviceConfirmed(doNotAskAgain: Boolean) {
        propertiesService.confirmDeviceRemoval = !doNotAskAgain
    }

    fun onCopyDeviceIdClicked(device: DeviceViewModel) {
        copyToClipboard(device.device.id)
    }

    fun onCopyDeviceAddressClicked(device: DeviceViewModel) {
        val address = device.device.address ?: return
        copyToClipboard(address.ip)
    }

    private fun onDevicesUpdated(model: List<Device>) {
        devices = model.map { it.toViewModel() }
        updateDeviceLists()
    }

    private fun updateDeviceLists() {
        val isScrcpyEnabled = propertiesService.scrcpyEnabled

        devices.forEach {
            it.isInProgress = connectingDevices.contains(it)
            it.isShareScreenButtonVisible = isScrcpyEnabled
        }

        if (!isAdbValid) {
            view?.showInvalidAdbLocationError()
        } else if (devices.isEmpty()) {
            view?.showEmptyMessage()
        } else {
            view?.showDevices(devices)
        }
    }

    private fun subscribeToDeviceList() {
        if (adbService.deviceListListener != null) {
            // Already subscribed
            return
        }
        adbService.deviceListListener = ::onDevicesUpdated
    }

    private fun unsubscribeFromDeviceList() {
        if (adbService.deviceListListener == null) {
            // Already unsubscribed
            return
        }
        adbService.deviceListListener = null
    }

    private fun subscribeToLogEvents() {
        logService.logVisibilityListener = ::updateLogVisibility
    }

    private fun unsubscribeFromLogEvents() {
        logService.logVisibilityListener = null
    }

    private fun subscribeToScrcpyEnabledState() {
        propertiesService.scrcpyEnabledListener = {
            updateDeviceLists()
        }
    }

    private fun unsubscribeFromScrcpyEnabledState() {
        propertiesService.scrcpyEnabledListener = null
    }

    private fun updateLogVisibility(isLogVisible: Boolean) {
        if (isLogVisible) {
            view?.openLog()
            logService.commandHistory.listener = object : CommandHistory.Listener {
                override fun onLogEntriesModified(entries: List<LogEntry>) {
                    view?.setLogEntries(entries)
                }
            }
        } else {
            view?.closeLog()
            logService.commandHistory.listener = null
        }
    }

    private fun subscribeToAdbLocationChanges() {
        propertiesService.adbLocationListener = { isValid ->
            isAdbValid = isValid
            if (!isValid) {
                unsubscribeFromDeviceList()
                devices = emptyList()
                view?.showInvalidAdbLocationError()
            } else {
                updateDeviceLists()
                if (isViewOpen) {
                    subscribeToDeviceList()
                }
            }
        }
    }

    private fun unsubscribeFromAdbLocationChanges() {
        propertiesService.adbLocationListener = null
    }

    private companion object {

        private fun MutableSet<Pair<String/*Unique ID*/, String/*IP address*/>>.contains(
            device: DeviceViewModel
        ): Boolean {
            return this.find { (uniqueId, address) -> uniqueId == device.uniqueId && address == device.address } != null
        }
    }
}
