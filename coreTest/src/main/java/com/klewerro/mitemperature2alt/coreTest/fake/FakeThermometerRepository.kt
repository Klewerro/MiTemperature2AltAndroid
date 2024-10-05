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
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    var thermometerStatus2 = ThermometerStatus(
        22.0f,
        52,
        2.23f
    )
    var isConnectToDeviceThrowingError = false
    var hourlyRecords: List<HourlyRecord>? = (0..15).map { i ->
        HourlyRecord(
            index = i,
            time = 1,
            temperatureMin = (10 + i).toFloat(),
            temperatureMax = (20 + i).toFloat(),
            humidityMin = 30 + i,
            humidityMax = 50 + i
        )
    }
    var lastIndexTotalRecords: LastIndexTotalRecords? = LastIndexTotalRecords(
        lastIndex = 0,
        totalRecords = hourlyRecords?.size ?: 0
    )

    val isScanningForDevicesInternal = MutableStateFlow(false)
    override val isScanningForDevices: StateFlow<Boolean> = isScanningForDevicesInternal

    val scannedDevicesInternal = MutableStateFlow<List<ThermometerScanResult>>(emptyList())
    override val scannedDevices: StateFlow<List<ThermometerScanResult>> = scannedDevicesInternal

    val connectedDevicesStatusesInternal =
        MutableStateFlow<Map<String, ThermometerStatus>>(emptyMap())
    override val connectedDevicesStatuses: StateFlow<Map<String, ThermometerStatus>> =
        connectedDevicesStatusesInternal.asStateFlow()

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
        // Empty
    }

    override suspend fun readCurrentThermometerStatus(deviceAddress: String): ThermometerStatus =
        thermometerStatus

    override suspend fun readLastIndexAndTotalRecords(
        deviceAddress: String
    ): LastIndexTotalRecords? {
        delay(operationDelay)
        return lastIndexTotalRecords
    }

    override suspend fun subscribeToCurrentThermometerStatus(
        deviceAddress: String,
        coroutineScope: CoroutineScope
    ) {
        coroutineScope.launch {
            var lastSynced = thermometerStatus2
            if (isActive) {
                connectedDevicesStatusesInternal.update {
//                    val syncItem = if (lastSynced == thermometerStatus) {
//                        thermometerStatus2
//                    } else {
//                        thermometerStatus
//                    }
//                    lastSynced = syncItem
                    it.plus(deviceAddress to thermometerStatus)
                }
                delay(operationDelay)
                ensureActive()
            }
        }.invokeOnCompletion {
            val i = 1
        }
    }

    override suspend fun subscribeToRssi(deviceAddress: String, coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            while (isActive) {
                rssiStrengthsInternal.update {
                    it.plus(deviceAddress to Random(System.currentTimeMillis()).nextInt(-99, -1))
                }
                delay(operationDelay)
                ensureActive()
            }
        }
    }

    override suspend fun subscribeToConnectionStatus(
        deviceAddress: String,
        coroutineScope: CoroutineScope
    ) {
        coroutineScope.launch {
            thermometerConnectionStatusesInternal.update {
                it.plus(deviceAddress to ThermometerConnectionStatus.CONNECTED)
            }
            delay(operationDelay)
            ensureActive()
        }
    }

    override suspend fun readThermometerHourlyRecords(
        coroutineScope: CoroutineScope,
        deviceAddress: String,
        startIndex: Int,
        progressUpdate: (Int, Int) -> Unit
    ): List<HourlyRecord>? {
        progressUpdate(0, hourlyRecords?.size ?: 0)
        delay(operationDelay)
        return hourlyRecords?.let { hourlyRecordsValue ->
            hourlyRecordsValue.forEach { record ->
                delay(50)
                progressUpdate(record.index + 1, hourlyRecordsValue.size)
            }
            hourlyRecords
        } ?: run {
            null
        }
    }
}
