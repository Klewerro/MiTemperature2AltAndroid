package com.klewerro.temperatureSensor.contracts

import com.klewerro.mitemperaturenospyware.domain.model.ThermometerScanResult
import com.klewerro.temperatureSensor.ThermometerDeviceBleClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow

interface ThermometerDevicesBleScanner {
    val isScanning: StateFlow<Boolean>
    val bleDevices: StateFlow<List<ThermometerScanResult>>

    fun scanForDevices(coroutineScope: CoroutineScope): Job
    suspend fun connectToDevice(coroutineScope: CoroutineScope, bleDeviceAddress: String): ThermometerDeviceBleClient
}
