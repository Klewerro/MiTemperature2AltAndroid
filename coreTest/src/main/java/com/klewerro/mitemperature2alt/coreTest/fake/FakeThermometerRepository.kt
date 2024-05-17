package com.klewerro.mitemperature2alt.coreTest.fake

import com.klewerro.mitemperature2alt.coreTest.generators.ThermometerScanResultsGenerator
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

    var operationDelay = 2_000L
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

    override fun scanForDevices(coroutineScope: CoroutineScope): Job {
        return coroutineScope.launch {
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
    }

    override suspend fun connectToDevice(coroutineScope: CoroutineScope, address: String) {
        delay(operationDelay)
        if (isConnectToDeviceThrowingError) {
            throw IllegalStateException("STUB exception!")
        }
    }

    override suspend fun readCurrentThermometerStatus(deviceAddress: String): ThermometerStatus {
        return thermometerStatus
    }

    override suspend fun subscribeToCurrentThermometerStatus(
        deviceAddress: String,
        coroutineScope: CoroutineScope
    ) {
        coroutineScope.launch {
            if (isActive) {
                connectedDevicesStatusesInternal.update {
                    it.toMutableMap().apply {
                        this.plus(
                            Pair(
                                deviceAddress,
                                thermometerStatus
                            )
                        )
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
                        this.plus(
                            Pair(
                                deviceAddress,
                                Random.nextInt(-99, -1)
                            )
                        )
                    }
                }
                delay(operationDelay)
            }
        }
    }
}
