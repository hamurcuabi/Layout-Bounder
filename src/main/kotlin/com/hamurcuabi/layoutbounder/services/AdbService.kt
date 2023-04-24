package com.hamurcuabi.layoutbounder.services

import com.hamurcuabi.layoutbounder.adb.ADB_DISPATCHER
import com.hamurcuabi.layoutbounder.adb.Adb
import com.hamurcuabi.layoutbounder.commandexecutor.RuntimeCommandExecutor
import com.hamurcuabi.layoutbounder.model.Device
import com.hamurcuabi.layoutbounder.utils.appCoroutineScope
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.service
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

class AdbService : Disposable {

    var deviceListListener: ((List<Device>) -> Unit)? = null
        set(value) {
            field = value
            if (value != null) {
                startPollingDevices()
            } else {
                stopPollingDevices()
            }
        }

    private val adb = Adb(RuntimeCommandExecutor(), service())
    private var devicePollingJob: Job? = null
    private val logService by lazy { service<LogService>() }
    private val properties by lazy { service<PropertiesService>() }

    suspend fun devices(): List<Device> {
        val devices = withContext(ADB_DISPATCHER) {
            adb.devices()
        }.map {
            it.hasLayoutBounds = adb.getLayoutBound(it.id).toBoolean()
            adb.toggleLayoutBound(it.hasLayoutBounds.toString(), it.id)
            it
        }
        return devices
    }

    suspend fun connect(device: Device) {
        adb.connect(device).collect { logEntry ->
            logService.commandHistory.add(logEntry)
        }
    }

    fun connect(ip: String, port: Int = properties.adbPort): String {
        return adb.connect(ip, port)
    }

    suspend fun disconnect(device: Device) {
        adb.disconnect(device).collect { logEntry ->
            logService.commandHistory.add(logEntry)
        }
    }

    fun restartAdb() {
        appCoroutineScope.launch(Dispatchers.Default) {
            adb.disconnectAllDevices().collect { logEntry ->
                logService.commandHistory.add(logEntry)
            }
            adb.killServer().collect { logEntry ->
                logService.commandHistory.add(logEntry)
            }
        }
    }

    fun toggleLayoutBound(state: String, deviceId: String) {
        appCoroutineScope.launch(Dispatchers.Default) {
            adb.toggleLayoutBound(state, deviceId).collect { logEntry ->
                logService.commandHistory.add(logEntry)
            }
        }
    }

    override fun dispose() {
        stopPollingDevices()
    }

    private fun startPollingDevices() {
        devicePollingJob?.cancel()
        devicePollingJob = appCoroutineScope.launch(Main) {
            devicesFlow().collect { devices ->
                deviceListListener?.invoke(devices)
            }
        }
    }

    private fun stopPollingDevices() {
        devicePollingJob?.cancel()
        devicePollingJob = null
    }

    private fun devicesFlow(): Flow<List<Device>> = flow {
        while (true) {
            emit(devices())
            delay(POLLING_INTERVAL_MILLIS)
        }
    }

    private companion object {
        const val POLLING_INTERVAL_MILLIS = 3000L
    }
}
