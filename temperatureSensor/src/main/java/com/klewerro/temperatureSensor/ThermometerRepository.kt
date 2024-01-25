package com.klewerro.temperatureSensor

import android.content.Context
import com.klewerro.temperatureSensor.model.CurrentThermometerStatus
import com.klewerro.temperatureSensor.model.ThermometerBleDevice
import com.klewerro.temperatureSensor.model.ThermometerDeviceConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ThermometerRepository(private val context: Context) {

    private val scanner = ThermometerDevicesBleScanner()
    private var deviceConnections: Map<String, ThermometerDeviceConnection> = HashMap()
        set(value) {
            field = value
            _connectedDevices.update { oldConnectedDevices ->
                convertDeviceConnectionsIntoListOfConnectedDevices(
                    thermometerDeviceConnections = value.values.toList(),
                    oldConnectedDevices = oldConnectedDevices
                )
            }
        }

    val scannedDevices = scanner.bleDevices
    val isScanningForDevices = scanner.isScanning

    private val _connectedDevices = MutableStateFlow<List<ThermometerBleDevice>>(emptyList())
    val connectedDevices = _connectedDevices.asStateFlow()

    private var _connectingToDeviceAddress = MutableStateFlow("")
    val connectingToDeviceAddress get() = _connectingToDeviceAddress.asStateFlow()

    fun scanForDevices(coroutineScope: CoroutineScope): Job =
        scanner.scanForDevices(context, coroutineScope)

    suspend fun connectToDevice(
        coroutineScope: CoroutineScope,
        address: String
    ) {
        if (_connectingToDeviceAddress.value.isNotBlank()) {
            throw IllegalStateException("Already connecting to other device!")
        }

        _connectingToDeviceAddress.update { address }
        val connectedDeviceResult = scanner.connectToDevice(context, coroutineScope, address)

        deviceConnections = deviceConnections.plus(
            Pair(
                connectedDeviceResult.thermometerScanResult.address,
                connectedDeviceResult
            )
        )
        _connectingToDeviceAddress.update { "" }
    }

    suspend fun readCurrentThermometerStatus(deviceAddress: String): CurrentThermometerStatus? {
        deviceConnections[deviceAddress]?.thermometerDeviceBleClient?.let { deviceClient ->
            val readStatus = deviceClient.readThermometerStatus()
            readStatus?.let { readStatusValue ->
                val deviceToUpdate = connectedDevices.value.first { it.address == deviceAddress }
                val updatedBleDevice = deviceToUpdate.copy(status = readStatusValue)
                updateDevice(deviceToUpdate, updatedBleDevice)
            }

            return readStatus
        } ?: run {
            return null
        }
    }

    suspend fun subscribeToCurrentThermometerStatus(deviceAddress: String, coroutineScope: CoroutineScope) {
        deviceConnections[deviceAddress]?.thermometerDeviceBleClient?.let { deviceClient ->
            coroutineScope.launch(Dispatchers.IO) {
                deviceClient.subscribeToThermometerStatus()?.collect { statusUpdate ->
                    val deviceToUpdate = connectedDevices.value.first { it.address == deviceAddress }
                    val updatedBleDevice = deviceToUpdate.copy(status = statusUpdate)
                    updateDevice(deviceToUpdate, updatedBleDevice)
                }
            }
        }
    }

    private fun convertDeviceConnectionsIntoListOfConnectedDevices(
        thermometerDeviceConnections: List<ThermometerDeviceConnection>,
        oldConnectedDevices: List<ThermometerBleDevice>
    ): List<ThermometerBleDevice> {
        val mappedValues = thermometerDeviceConnections.map {
            ThermometerBleDevice.fromScanResult(it.thermometerScanResult)
        }

        val result = mutableListOf<ThermometerBleDevice>()
        mappedValues.plus(oldConnectedDevices).forEach { device ->
            if (result.any { it.address == device.address }) {
                val alreadyAddedResultWithoutStatus = result.firstOrNull {
                    it.status == null && device.status != null
                }
                alreadyAddedResultWithoutStatus?.let {
                    result.remove(it)
                    result.add(device)
                }
            } else {
                result.add(device)
            }
        }

        return result
    }

    private fun updateDevice(oldDevice: ThermometerBleDevice, updatedDevice: ThermometerBleDevice) {
        val deviceToUpdateIndex = connectedDevices.value.indexOfFirst { it.address == oldDevice.address }
        _connectedDevices.update {
            it.toMutableList().apply {
                set(deviceToUpdateIndex, updatedDevice)
            }.toList()
        }
    }
}
