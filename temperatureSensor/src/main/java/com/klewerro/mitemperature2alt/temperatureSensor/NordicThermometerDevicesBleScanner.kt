package com.klewerro.mitemperature2alt.temperatureSensor

import android.annotation.SuppressLint
import android.content.Context
import com.klewerro.mitemperature2alt.domain.model.ScannedDeviceStatus
import com.klewerro.mitemperature2alt.domain.model.ThermometerScanResult
import com.klewerro.mitemperature2alt.temperatureSensor.contracts.ThermometerDevicesBleScanner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import no.nordicsemi.android.kotlin.ble.client.main.callback.ClientBleGatt
import no.nordicsemi.android.kotlin.ble.scanner.BleScanner
import no.nordicsemi.android.kotlin.ble.scanner.aggregator.BleScanResultAggregator
import timber.log.Timber

@SuppressLint("MissingPermission")
class NordicThermometerDevicesBleScanner(
    private val context: Context
) : ThermometerDevicesBleScanner {

    private val aggregator = BleScanResultAggregator()

    private val _isScanning = MutableStateFlow(false)
    override val isScanning = _isScanning.asStateFlow()

    private val _bleDevices = MutableStateFlow<List<ThermometerScanResult>>(emptyList())
    override val bleDevices = _bleDevices.asStateFlow()

    override fun scanForDevices(coroutineScope: CoroutineScope): Job {
        _isScanning.update { true }
        return BleScanner(context)
            .scan()
            .filter { it.device.name == BleConstants.DEVICE_NAME }
            .map { aggregator.aggregate(it) }
            .map { bleScanResultsList ->
                bleScanResultsList
                    .sortedByDescending { it.highestRssi }
                    .map { bleScanResult ->
                        ThermometerScanResult(
                            bleScanResult.device.name ?: "",
                            bleScanResult.device.address,
                            bleScanResult.scanResult.last().rssi,
                            ScannedDeviceStatus.NOT_CONNECTED
                        )
                    }
            }
            .distinctUntilChanged()
            .onEach { list ->
                _bleDevices.update { list }
            }
            .onCompletion {
                _isScanning.update { false }
            }
            .launchIn(coroutineScope)
    }

    override suspend fun scanAndConnect(
        coroutineScope: CoroutineScope,
        address: String
    ): ThermometerDeviceBleClient {
        _isScanning.update { true }
        val bleDevice = BleScanner(context)
            .scan()
            .filter { it.device.name == BleConstants.DEVICE_NAME && it.device.address == address }
            .onCompletion {
                _isScanning.update { false }
            }
            .first()
            .device

        val gattConnection = ClientBleGatt.connect(context, bleDevice, coroutineScope)
        val client = ThermometerDeviceBleClient(gattConnection).apply {
            discoverDeviceOperations()
        }
        return client
    }

    override suspend fun connectToDevice(
        coroutineScope: CoroutineScope,
        bleDeviceAddress: String
    ): ThermometerDeviceBleClient {
        val foundBleDevices = aggregator.results
        val bleDevice = foundBleDevices.first { it.device.address == bleDeviceAddress }.device

        val gattConnection = ClientBleGatt.connect(context, bleDevice, coroutineScope)
        val isConnected = gattConnection.isConnected
        Timber.d("connectToDevice ${bleDevice.name} connected: $isConnected")

        val client = ThermometerDeviceBleClient(gattConnection).apply {
            discoverDeviceOperations()
        }

        return client
    }
}
