package com.klewerro.mitemperaturenospyware.domain.repository

import com.klewerro.mitemperaturenospyware.domain.model.ThermometerScanResult
import com.klewerro.mitemperaturenospyware.domain.model.ThermometerStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow

interface ThermometerRepository {
    val isScanningForDevices: StateFlow<Boolean>
    val scannedDevices: StateFlow<List<ThermometerScanResult>>
    val connectingToDeviceAddress: StateFlow<String>
    val connectedDevicesStatuses: StateFlow<Map<String, ThermometerStatus>>
    val rssiStrengths: StateFlow<Map<String, Int>>

    fun scanForDevices(coroutineScope: CoroutineScope): Job
    suspend fun connectToDevice(
        coroutineScope: CoroutineScope,
        address: String
    )

    suspend fun readCurrentThermometerStatus(deviceAddress: String): ThermometerStatus?
    suspend fun subscribeToCurrentThermometerStatus(deviceAddress: String, coroutineScope: CoroutineScope)
    suspend fun subscribeToRssi(deviceAddress: String, coroutineScope: CoroutineScope)
}
