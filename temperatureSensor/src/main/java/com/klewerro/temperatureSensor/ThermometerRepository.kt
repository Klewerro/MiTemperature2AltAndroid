package com.klewerro.temperatureSensor

import android.content.Context
import com.klewerro.temperatureSensor.model.ThermometerBleDevice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ThermometerRepository(private val context: Context) {

    private val scanner = ThermometerDevicesBleScanner()
    private var deviceConnections: Map<ThermometerBleDevice, ThermometerDeviceBleClient> = HashMap()
        set(value) {
            field = value
            _connectedDevices.update { value.keys.toList() }
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
                connectedDeviceResult.thermometerBleDevice,
                ThermometerDeviceBleClient(connectedDeviceResult.gattClient)
            )
        )
    }
}
