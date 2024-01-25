package com.klewerro.mitemperaturenospyware.domain.repository

import com.klewerro.mitemperaturenospyware.domain.model.CurrentThermometerStatus
import com.klewerro.mitemperaturenospyware.domain.model.ThermometerBleDevice
import com.klewerro.mitemperaturenospyware.domain.model.ThermometerScanResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow

interface ThermometerRepository {
    val isScanningForDevices: StateFlow<Boolean>
    val scannedDevices: StateFlow<List<ThermometerScanResult>>
    val connectedDevices: StateFlow<List<ThermometerBleDevice>>
    val connectingToDeviceAddress: StateFlow<String>

    fun scanForDevices(coroutineScope: CoroutineScope): Job
    suspend fun connectToDevice(
        coroutineScope: CoroutineScope,
        address: String
    )

    suspend fun readCurrentThermometerStatus(deviceAddress: String): CurrentThermometerStatus?
    suspend fun subscribeToCurrentThermometerStatus(deviceAddress: String, coroutineScope: CoroutineScope)
}
