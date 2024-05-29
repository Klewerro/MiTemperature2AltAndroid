package com.klewerro.mitemperature2alt.domain.repository

import com.klewerro.mitemperature2alt.domain.model.ThermometerConnectionStatus
import com.klewerro.mitemperature2alt.domain.model.ThermometerScanResult
import com.klewerro.mitemperature2alt.domain.model.ThermometerStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow

interface ThermometerRepository {
    val isScanningForDevices: StateFlow<Boolean>
    val scannedDevices: StateFlow<List<ThermometerScanResult>>
    val connectedDevicesStatuses: StateFlow<Map<String, ThermometerStatus>>
    val rssiStrengths: StateFlow<Map<String, Int>>
    val thermometerConnectionStatuses: StateFlow<Map<String, ThermometerConnectionStatus>>

    fun scanForDevices(coroutineScope: CoroutineScope): Job
    suspend fun connectToDevice(coroutineScope: CoroutineScope, address: String)
    suspend fun scanAndConnect(coroutineScope: CoroutineScope, address: String)

    suspend fun readCurrentThermometerStatus(deviceAddress: String): ThermometerStatus?
    suspend fun subscribeToCurrentThermometerStatus(
        deviceAddress: String,
        coroutineScope: CoroutineScope
    )
    suspend fun subscribeToRssi(deviceAddress: String, coroutineScope: CoroutineScope)
    suspend fun subscribeToConnectionStatus(deviceAddress: String, coroutineScope: CoroutineScope)
}
