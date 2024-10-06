package com.klewerro.mitemperature2alt.temperatureSensor

import com.klewerro.mitemperature2alt.domain.model.HourlyRecord
import com.klewerro.mitemperature2alt.domain.model.LastIndexTotalRecords
import com.klewerro.mitemperature2alt.domain.model.ThermometerConnectionStatus
import com.klewerro.mitemperature2alt.domain.model.ThermometerStatus
import com.klewerro.mitemperature2alt.domain.repository.ThermometerRepository
import com.klewerro.mitemperature2alt.temperatureSensor.contracts.ThermometerDevicesBleScanner
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toCollection
import kotlinx.coroutines.flow.transformWhile
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import no.nordicsemi.android.kotlin.ble.core.data.GattConnectionState
import timber.log.Timber

class NordicBleThermometerRepository(private val scanner: ThermometerDevicesBleScanner) :
    ThermometerRepository {

    private var connectedDevicesClients: Map<String, ThermometerDeviceBleClient> = HashMap()

    override val scannedDevices = scanner.bleDevices
    override val isScanningForDevices = scanner.isScanning

    private var _connectedDevicesStatuses = MutableStateFlow<Map<String, ThermometerStatus>>(
        emptyMap()
    )
    override val connectedDevicesStatuses = _connectedDevicesStatuses.asStateFlow()

    private var _thermometerConnectionStatuses =
        MutableStateFlow<Map<String, ThermometerConnectionStatus>>(emptyMap())
    override val thermometerConnectionStatuses = _thermometerConnectionStatuses.asStateFlow()

    private var _rssiStrengths = MutableStateFlow<Map<String, Int>>(emptyMap())
    override val rssiStrengths = _rssiStrengths.asStateFlow()

    override fun scanForDevices(coroutineScope: CoroutineScope): Job =
        scanner.scanForDevices(coroutineScope)

    override suspend fun connectToDevice(coroutineScope: CoroutineScope, address: String) {
        _thermometerConnectionStatuses.updateUsingMutableMap(
            address,
            ThermometerConnectionStatus.CONNECTING
        )
        val connectedDeviceClient = scanner.connectToDevice(coroutineScope, address)
        connectedDeviceClient.readThermometerStatus()?.let { thermometerStatus ->
            _connectedDevicesStatuses.update {
                it.plus(address to thermometerStatus)
            }
        }

        connectedDevicesClients = connectedDevicesClients.plus(address to connectedDeviceClient)
        observeConnectionStatus(address, connectedDeviceClient, coroutineScope)
    }

    override suspend fun scanAndConnect(coroutineScope: CoroutineScope, address: String) {
        withTimeout(BleConstants.CONNECT_THERMOMETER_TIMEOUT) {
            _thermometerConnectionStatuses.updateUsingMutableMap(
                address,
                ThermometerConnectionStatus.CONNECTING
            )
            try {
                val deviceClient = scanner.scanAndConnect(coroutineScope, address)
                deviceClient.readThermometerStatus()?.let { thermometerStatus ->
                    _connectedDevicesStatuses.update {
                        it.plus(address to thermometerStatus)
                    }
                }
                connectedDevicesClients = connectedDevicesClients.plus(address to deviceClient)
                observeConnectionStatus(address, deviceClient, coroutineScope)
            } catch (illegalStateException: IllegalStateException) {
                Timber.e(illegalStateException)
                _thermometerConnectionStatuses.updateUsingMutableMap(
                    address,
                    ThermometerConnectionStatus.DISCONNECTED
                )
                throw illegalStateException
            }
        }
    }

    override fun disconnect(deviceAddress: String) {
        connectedDevicesClients[deviceAddress]?.disconnect()
    }

    override suspend fun readCurrentThermometerStatus(deviceAddress: String): ThermometerStatus? {
        connectedDevicesClients[deviceAddress]?.let { deviceClient ->
            val readStatus = deviceClient.readThermometerStatus()
            readStatus?.let { readStatusValue ->
                _connectedDevicesStatuses.update {
                    it.plus(deviceAddress to readStatusValue)
                }
            }
            return readStatus
        } ?: run {
            return null
        }
    }

    override suspend fun readLastIndexAndTotalRecords(
        deviceAddress: String
    ): LastIndexTotalRecords? {
        connectedDevicesClients[deviceAddress]?.let { deviceClient ->
            return deviceClient.readLastIndexAndTotalRecords()
        } ?: run {
            return null
        }
    }

    override suspend fun readThermometerHourlyRecords(
        coroutineScope: CoroutineScope,
        deviceAddress: String,
        startIndex: Int,
        progressUpdate: (Int, Int) -> Unit
    ): List<HourlyRecord>? {
        connectedDevicesClients[deviceAddress]?.let { deviceClient ->
            val lastIndexTotalRecords = deviceClient.readLastIndexAndTotalRecords()
            val totalRecords = lastIndexTotalRecords?.totalRecords ?: startIndex

            if (totalRecords == 0) {
                return null
            }

            progressUpdate(0, totalRecords)
            return collectHourlyRecords(
                coroutineScope = coroutineScope,
                deviceClient = deviceClient,
                totalRecords = totalRecords
            ) { currentItemNumber ->
                progressUpdate(currentItemNumber, totalRecords)
            }
        } ?: run {
            return null
        }
    }

    override suspend fun subscribeToCurrentThermometerStatus(
        deviceAddress: String,
        coroutineScope: CoroutineScope
    ) {
        connectedDevicesClients[deviceAddress]?.let { deviceClient ->
            deviceClient.subscribeToThermometerStatus()
                ?.let {
                    Timber.d("Subscribed to thermometer status updates.")
                    it
                }
                ?.onEach { statusUpdate ->
                    Timber.d("Thermometer status update: $statusUpdate")
                    _connectedDevicesStatuses.update {
                        it.plus(deviceAddress to statusUpdate)
                    }
                }
                ?.distinctUntilChanged()
                ?.launchIn(coroutineScope)
                ?.invokeOnCompletion {
                    _connectedDevicesStatuses.update {
                        it.minus(deviceAddress)
                    }
                }
        }
    }

    override suspend fun subscribeToConnectionStatus(
        deviceAddress: String,
        coroutineScope: CoroutineScope
    ) {
        connectedDevicesClients[deviceAddress]?.let { deviceClient ->
            deviceClient.connectionState
                .onEach { gattConnectionState ->
                    _thermometerConnectionStatuses.updateUsingMutableMap(
                        deviceAddress,
                        when (gattConnectionState) {
                            GattConnectionState.STATE_DISCONNECTED ->
                                ThermometerConnectionStatus.DISCONNECTED

                            GattConnectionState.STATE_CONNECTING ->
                                ThermometerConnectionStatus.CONNECTING

                            GattConnectionState.STATE_CONNECTED ->
                                ThermometerConnectionStatus.CONNECTED

                            GattConnectionState.STATE_DISCONNECTING ->
                                ThermometerConnectionStatus.DISCONNECTING
                        }
                    )
                }
                .launchIn(coroutineScope)
                .invokeOnCompletion {
                    _thermometerConnectionStatuses.update {
                        it.minus(deviceAddress)
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
                        it.plus(deviceAddress to rssi)
                    }
                    delay(BleConstants.READ_RSSI_DELAY)
                }
            }.invokeOnCompletion {
                _rssiStrengths.update {
                    it.minus(deviceAddress)
                }
            }
        }
    }

    private fun observeConnectionStatus(
        address: String,
        thermometerClient: ThermometerDeviceBleClient,
        coroutineScope: CoroutineScope
    ) {
        thermometerClient
            .connectionState
            .onEach { gattConnectionState ->
                Timber.d("ConnectionState: ${gattConnectionState.name}")
                val mappedConnectionStatus = when (gattConnectionState) {
                    GattConnectionState.STATE_CONNECTING ->
                        ThermometerConnectionStatus.CONNECTING

                    GattConnectionState.STATE_CONNECTED ->
                        ThermometerConnectionStatus.CONNECTED

                    GattConnectionState.STATE_DISCONNECTING ->
                        ThermometerConnectionStatus.DISCONNECTING

                    GattConnectionState.STATE_DISCONNECTED -> {
                        connectedDevicesClients = connectedDevicesClients.minus(address)
                        ThermometerConnectionStatus.DISCONNECTED
                    }
                }
                _thermometerConnectionStatuses.updateUsingMutableMap(
                    address,
                    mappedConnectionStatus
                )
            }.launchIn(coroutineScope)
            .invokeOnCompletion {
                _thermometerConnectionStatuses.update {
                    it.minus(address)
                }
            }
    }

    private fun <V, K> MutableStateFlow<Map<V, K>>.updateUsingMutableMap(key: V, value: K) {
        this.update {
            it.toMutableMap().apply {
                this[key] = value
            }.toMap()
        }
    }

    private suspend fun collectHourlyRecords(
        coroutineScope: CoroutineScope,
        deviceClient: ThermometerDeviceBleClient,
        totalRecords: Int,
        currentItemNumberUpdate: (Int) -> Unit
    ) = suspendCoroutine<List<HourlyRecord>> { continuation ->
        val collectedRecords = mutableListOf<HourlyRecord>()
        var counter: Int
        coroutineScope.launch {
            deviceClient.subscribeToThermometerHourlyRecords()
                ?.cancellable()
                ?.transformWhile {
                    counter = it.index + 1
                    Timber.d(
                        "HourlyRecord collected: time: ${it.dateTime} [${it.index + 1}/$totalRecords]"
                    )
                    emit(it)
                    currentItemNumberUpdate(counter)
                    counter < totalRecords
                }
                ?.onEach {
                    coroutineScope.ensureActive()
                }
                ?.catch {
                    Timber.e(it, "Exception occurred during ")
                }
                ?.toCollection(collectedRecords)
        }.invokeOnCompletion {
            continuation.resume(collectedRecords)
        }
    }
}
