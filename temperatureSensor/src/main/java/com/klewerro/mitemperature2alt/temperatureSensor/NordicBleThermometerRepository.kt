package com.klewerro.mitemperature2alt.temperatureSensor

import com.klewerro.mitemperature2alt.domain.model.ThermometerStatus
import com.klewerro.mitemperature2alt.domain.repository.ThermometerRepository
import com.klewerro.mitemperature2alt.temperatureSensor.contracts.ThermometerDevicesBleScanner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class NordicBleThermometerRepository(
    private val scanner: ThermometerDevicesBleScanner
) : ThermometerRepository {

    private var connectedDevicesClients: Map<String, ThermometerDeviceBleClient> = HashMap()

    override val scannedDevices = scanner.bleDevices
    override val isScanningForDevices = scanner.isScanning

    private var _connectingToDeviceAddress = MutableStateFlow("")
    override val connectingToDeviceAddress = _connectingToDeviceAddress.asStateFlow()

    override fun scanForDevices(coroutineScope: CoroutineScope): Job =
        scanner.scanForDevices(coroutineScope)

    private var _connectedDevicesStatuses = MutableStateFlow<Map<String, ThermometerStatus>>(
        emptyMap()
    )
    override val connectedDevicesStatuses = _connectedDevicesStatuses.asStateFlow()

    private var _rssiStrengths = MutableStateFlow<Map<String, Int>>(emptyMap())
    override val rssiStrengths = _rssiStrengths.asStateFlow()

    override suspend fun connectToDevice(coroutineScope: CoroutineScope, address: String) {
        if (_connectingToDeviceAddress.value.isNotBlank()) {
            throw IllegalStateException("Already connecting to other device!")
        }
        if (_connectingToDeviceAddress.value == address) {
            return
        }

        _connectingToDeviceAddress.update { address }
        val connectedDeviceClient = scanner.connectToDevice(coroutineScope, address)
        connectedDeviceClient.readThermometerStatus()?.let { thermometerStatus ->
            _connectedDevicesStatuses.update {
                it.plus(
                    Pair(
                        address,
                        thermometerStatus
                    )
                )
            }
        }

        connectedDevicesClients = connectedDevicesClients.plus(
            Pair(
                address,
                connectedDeviceClient
            )
        )
        _connectingToDeviceAddress.update { "" }
    }

    override suspend fun readCurrentThermometerStatus(deviceAddress: String): ThermometerStatus? {
        connectedDevicesClients[deviceAddress]?.let { deviceClient ->
            val readStatus = deviceClient.readThermometerStatus()
            readStatus?.let { readStatusValue ->
                _connectedDevicesStatuses.update {
                    it.plus(
                        Pair(
                            deviceAddress,
                            readStatusValue
                        )
                    )
                }
            }
            return readStatus
        } ?: run {
            return null
        }
    }

    override suspend fun subscribeToCurrentThermometerStatus(
        deviceAddress: String,
        coroutineScope: CoroutineScope
    ) {
        connectedDevicesClients[deviceAddress]?.let { deviceClient ->
            coroutineScope.launch(Dispatchers.IO) {
                deviceClient.subscribeToThermometerStatus()?.collect { statusUpdate ->
                    _connectedDevicesStatuses.update {
                        it.plus(
                            Pair(
                                deviceAddress,
                                statusUpdate
                            )
                        )
                    }
                }
            }
        }
    }

    override suspend fun subscribeToRssi(deviceAddress: String, coroutineScope: CoroutineScope) {
        connectedDevicesClients[deviceAddress]?.let { deviceClient ->
            coroutineScope.launch(Dispatchers.IO) {
                while (this.isActive) {
                    val rssi = deviceClient.readRssi()
                    _rssiStrengths.update {
                        it.plus(
                            Pair(
                                deviceAddress,
                                rssi
                            )
                        )
                    }
                    delay(BleConstants.READ_RSSI_DELAY)
                }
            }
        }
    }
}
