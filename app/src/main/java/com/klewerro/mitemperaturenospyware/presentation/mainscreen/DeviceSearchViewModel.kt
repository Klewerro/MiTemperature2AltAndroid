package com.klewerro.mitemperaturenospyware.presentation.mainscreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klewerro.mitemperaturenospyware.presentation.model.ConnectionStatus
import com.klewerro.mitemperaturenospyware.presentation.model.PermissionStatus
import com.klewerro.mitemperaturenospyware.presentation.model.ThermometerUiDevice
import com.klewerro.temperatureSensor.ThermometerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeviceSearchViewModel @Inject constructor(
    private val thermometerRepository: ThermometerRepository
) : ViewModel() {

    private var scanningBleDevicesJob: Job? = null

    val isScanningForDevices = thermometerRepository.isScanningForDevices
    private val scannedDevices = thermometerRepository.scannedDevices
    val connectedDevices = thermometerRepository.connectedDevices

    val devicesCombined = combine(
        scannedDevices,
        connectedDevices,
        thermometerRepository.connectingToDeviceAddress
    ) { scanned, connected, currentlyConnectingDeviceAddress ->
        val connectingUiDeviceIndex =
            scanned.indexOfFirst { it.address == currentlyConnectingDeviceAddress }
        scanned.mapIndexed { mapIndex, thermometerBleScanResult ->
            val isConnected = connected.any { it.address == thermometerBleScanResult.address }
            ThermometerUiDevice(
                name = thermometerBleScanResult.name,
                address = thermometerBleScanResult.address,
                rssi = thermometerBleScanResult.rssi,
                connectionStatus = when {
                    connectingUiDeviceIndex > -1 && connectingUiDeviceIndex == mapIndex -> ConnectionStatus.CONNECTING
                    isConnected -> ConnectionStatus.CONNECTED
                    else -> ConnectionStatus.NOT_CONNECTED
                },
                status = null
            )
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    var permissionGrantStatus by mutableStateOf(PermissionStatus.DECLINED)

    fun scanForDevices() {
        if (scanningBleDevicesJob != null) {
            stopScanForDevices()
        }
        viewModelScope.launch(Dispatchers.IO) {
            scanningBleDevicesJob = thermometerRepository.scanForDevices(this)
        }
    }

    fun stopScanForDevices() {
        scanningBleDevicesJob?.cancel()
        scanningBleDevicesJob = null
    }
}
