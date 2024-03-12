package com.klewerro.mitemperaturenospyware.coreTest.fake

import com.klewerro.mitemperaturenospyware.domain.model.ThermometerScanResult
import com.klewerro.mitemperaturenospyware.domain.model.ThermometerStatus
import com.klewerro.mitemperaturenospyware.domain.repository.ThermometerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FakeThermometerRepository : ThermometerRepository {

    var operationDelay = 2_000L

    val isScanningForDevicesInternal = MutableStateFlow(false)
    override val isScanningForDevices: StateFlow<Boolean> = isScanningForDevicesInternal

    val scannedDevicesInternal = MutableStateFlow<List<ThermometerScanResult>>(emptyList())
    override val scannedDevices: StateFlow<List<ThermometerScanResult>> = scannedDevicesInternal

    val connectingToDeviceAddressInternal = MutableStateFlow("")
    override val connectingToDeviceAddress: StateFlow<String> = connectingToDeviceAddressInternal

    val connectedDevicesStatusesInternal =
        MutableStateFlow<Map<String, ThermometerStatus>>(emptyMap())
    override val connectedDevicesStatuses: StateFlow<Map<String, ThermometerStatus>> =
        connectedDevicesStatusesInternal

    val rssiStrengthsInternal = MutableStateFlow<Map<String, Int>>(emptyMap())
    override val rssiStrengths: StateFlow<Map<String, Int>> = rssiStrengthsInternal

    override fun scanForDevices(coroutineScope: CoroutineScope): Job {
        return coroutineScope.launch {
            isScanningForDevicesInternal.update { true }
        }.apply {
            invokeOnCompletion {
                isScanningForDevicesInternal.update { false }
            }
        }
    }

    override suspend fun connectToDevice(coroutineScope: CoroutineScope, address: String) {
        if (connectingToDeviceAddressInternal.value.isNotBlank()) {
            throw IllegalStateException("Already connecting to other device!")
        }
        connectingToDeviceAddressInternal.update { address }
        delay(operationDelay)
        connectingToDeviceAddressInternal.update { "" }
    }

    override suspend fun readCurrentThermometerStatus(deviceAddress: String): ThermometerStatus? {
        TODO("Not yet implemented")
    }

    override suspend fun subscribeToCurrentThermometerStatus(
        deviceAddress: String,
        coroutineScope: CoroutineScope
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun subscribeToRssi(deviceAddress: String, coroutineScope: CoroutineScope) {
        TODO("Not yet implemented")
    }
}
