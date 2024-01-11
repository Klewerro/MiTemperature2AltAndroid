package com.klewerro.temperatureSensor

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.klewerro.temperatureSensor.model.ThermometerBleDevice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import no.nordicsemi.android.kotlin.ble.client.main.callback.ClientBleGatt
import no.nordicsemi.android.kotlin.ble.scanner.BleScanner
import no.nordicsemi.android.kotlin.ble.scanner.aggregator.BleScanResultAggregator

@SuppressLint("MissingPermission")
class ThermometerDevicesBleScanner {

    private val aggregator = BleScanResultAggregator()
    private var gattConnection: ClientBleGatt? = null

    private val _bleDevices = MutableStateFlow<List<ThermometerBleDevice>>(emptyList())
    val bleDevices = _bleDevices.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning = _isScanning.asStateFlow()

    fun scanForDevices(context: Context, coroutineScope: CoroutineScope): Job {
        _isScanning.update { true }
        return BleScanner(context)
            .scan()
            .filter { it.device.name == BleConstants.DEVICE_NAME }
            .map { aggregator.aggregate(it) }
            .map { bleScanResultsList ->
                bleScanResultsList
                    .sortedByDescending { it.highestRssi }
                    .map { bleScanResult ->
                        ThermometerBleDevice(
                            bleScanResult.device.name ?: "",
                            bleScanResult.device.address,
                            bleScanResult.scanResult.last().rssi
                        )
                    }
            }
            .distinctUntilChanged()
            .onEach { list ->
                _bleDevices.update { list }
            }
            .onCompletion {
                _isScanning.update { false }
                _bleDevices.update { emptyList() }
            }
            .launchIn(coroutineScope)
    }

    suspend fun connectToDevice(context: Context, coroutineScope: CoroutineScope, thermometerBleDevice: ThermometerBleDevice) {
        val foundBleDevices = aggregator.results
        val bleDevice = foundBleDevices.first { it.device.address == thermometerBleDevice.address }.device

        gattConnection = ClientBleGatt.connect(context, bleDevice, coroutineScope)
        val isConnected = gattConnection?.isConnected == true
        Log.d("ThermometerDevicesBleScanner", "connectToDevice ${bleDevice.name} connected: $isConnected")
    }
}
