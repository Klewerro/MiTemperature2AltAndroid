package com.klewerro.mitemperature2alt.coreTest.fake

import com.klewerro.mitemperature2alt.coreTest.generators.ThermometerScanResultsGenerator
import com.klewerro.mitemperature2alt.domain.model.HourlyRecord
import com.klewerro.mitemperature2alt.domain.model.LastIndexTotalRecords
import com.klewerro.mitemperature2alt.domain.model.ThermometerConnectionStatus
import com.klewerro.mitemperature2alt.domain.model.ThermometerScanResult
import com.klewerro.mitemperature2alt.domain.model.ThermometerStatus
import com.klewerro.mitemperature2alt.domain.repository.ThermometerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

class FakeThermometerRepository : ThermometerRepository {

    var operationDelay = 1_000L
    var thermometerStatus = ThermometerStatus(
        21.0f,
        51,
        1.23f
    )
    var isConnectToDeviceThrowingError = false

    val isScanningForDevicesInternal = MutableStateFlow(false)
    override val isScanningForDevices: StateFlow<Boolean> = isScanningForDevicesInternal

    val scannedDevicesInternal = MutableStateFlow<List<ThermometerScanResult>>(emptyList())
    override val scannedDevices: StateFlow<List<ThermometerScanResult>> = scannedDevicesInternal

    val connectedDevicesStatusesInternal =
        MutableStateFlow<Map<String, ThermometerStatus>>(emptyMap())
    override val connectedDevicesStatuses: StateFlow<Map<String, ThermometerStatus>> =
        connectedDevicesStatusesInternal

    val rssiStrengthsInternal = MutableStateFlow<Map<String, Int>>(emptyMap())
    override val rssiStrengths: StateFlow<Map<String, Int>> = rssiStrengthsInternal

    val thermometerConnectionStatusesInternal =
        MutableStateFlow<Map<String, ThermometerConnectionStatus>>(emptyMap())
    override val thermometerConnectionStatuses: StateFlow<Map<String, ThermometerConnectionStatus>> =
        thermometerConnectionStatusesInternal

    override fun scanForDevices(coroutineScope: CoroutineScope): Job = coroutineScope.launch {
        isScanningForDevicesInternal.update { true }
        delay(100)
        scannedDevicesInternal.update {
            it.toMutableList().apply {
                add(ThermometerScanResultsGenerator.scanResult1)
            }
        }
        while (isActive) {
            delay(100)
        }
    }.apply {
        invokeOnCompletion {
            isScanningForDevicesInternal.update { false }
        }
    }

    override suspend fun connectToDevice(coroutineScope: CoroutineScope, address: String) {
        delay(operationDelay)
        if (isConnectToDeviceThrowingError) {
            throw IllegalStateException("STUB exception!")
        }
    }

    override suspend fun scanAndConnect(coroutineScope: CoroutineScope, address: String) {
        try {
            isScanningForDevicesInternal.update { true }
            delay(operationDelay)
            scannedDevicesInternal.update {
                it.toMutableList().apply {
                    add(ThermometerScanResultsGenerator.scanResult1)
                }.toList()
            }
            thermometerConnectionStatusesInternal.update {
                it.toMutableMap().apply {
                    put(address, ThermometerConnectionStatus.CONNECTING)
                }
            }

            delay(operationDelay)
            if (isConnectToDeviceThrowingError) {
                throw IllegalStateException("STUB!")
            }

            thermometerConnectionStatusesInternal.update {
                it.toMutableMap().apply {
                    put(address, ThermometerConnectionStatus.CONNECTED)
                }
            }
        } catch (illegalState: IllegalStateException) {
            thermometerConnectionStatusesInternal.update {
                it.plus(address to ThermometerConnectionStatus.DISCONNECTED)
            }
            throw illegalState
        } finally {
            isScanningForDevicesInternal.update { false }
        }
    }

    override fun disconnect(deviceAddress: String) {
        TODO("Not yet implemented")
    }

    override suspend fun readCurrentThermometerStatus(deviceAddress: String): ThermometerStatus =
        thermometerStatus

    override suspend fun readLastIndexAndTotalRecords(
        deviceAddress: String
    ): LastIndexTotalRecords? {
        TODO("Not yet implemented")
    }

    override suspend fun subscribeToCurrentThermometerStatus(
        deviceAddress: String,
        coroutineScope: CoroutineScope
    ) {
        coroutineScope.launch {
            if (isActive) {
                connectedDevicesStatusesInternal.update {
                    it.toMutableMap().apply {
                        this.plus(deviceAddress to thermometerStatus)
                    }
                }
                delay(operationDelay)
            }
        }
    }

    override suspend fun subscribeToRssi(deviceAddress: String, coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            if (isActive) {
                rssiStrengthsInternal.update {
                    it.toMutableMap().apply {
                        this.plus(deviceAddress to Random.nextInt(-99, -1))
                    }
                }
                delay(operationDelay)
            }
        }
    }

    override suspend fun subscribeToConnectionStatus(
        deviceAddress: String,
        coroutineScope: CoroutineScope
    ) {
        coroutineScope.launch {
            thermometerConnectionStatusesInternal.update {
                it.toMutableMap().apply {
                    this.plus(deviceAddress to ThermometerConnectionStatus.CONNECTED)
                }
            }
            delay(operationDelay)
        }
    }

    override suspend fun readThermometerHourlyRecords(
        coroutineScope: CoroutineScope,
        deviceAddress: String,
        startIndex: Int,
        progressUpdate: (Int, Int) -> Unit
    ): List<HourlyRecord>? {
        TODO("Not yet implemented")
    }
}
