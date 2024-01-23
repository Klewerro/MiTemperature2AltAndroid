package com.klewerro.mitemperaturenospyware.presentation.mainscreen

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klewerro.mitemperaturenospyware.presentation.model.PermissionStatus
import com.klewerro.mitemperaturenospyware.presentation.model.ThermometerUiDevice
import com.klewerro.temperatureSensor.ThermometerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DeviceSearchViewModel : ViewModel() {

    private val thermometerRepository = ThermometerRepository()
    private var scanningBleDevicesJob: Job? = null

    val isScanningForDevices = thermometerRepository.isScanningForDevices
    val scannedDevices = thermometerRepository.scannedDevices
    val connectedDevices = thermometerRepository.connectedDevices

    val devicesCombined = scannedDevices.combine(connectedDevices) { scanned, connected ->
        scanned.map { thermometerBleDevice ->
            val isConnected = connected.any { it.address == thermometerBleDevice.address }
            ThermometerUiDevice(
                name = thermometerBleDevice.name,
                address = thermometerBleDevice.address,
                rssi = thermometerBleDevice.rssi,
                isConnected = isConnected
            )
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    var permissionGrantStatus by mutableStateOf(PermissionStatus.DECLINED)

    fun scanForDevices(context: Context) {
        if (scanningBleDevicesJob != null) {
            stopScanForDevices()
        }
        viewModelScope.launch(Dispatchers.IO) {
            scanningBleDevicesJob = thermometerRepository.scanForDevices(context, this)
        }
    }

    fun stopScanForDevices() {
        scanningBleDevicesJob?.cancel()
        scanningBleDevicesJob = null
    }

    fun connectToDevice(context: Context, thermometerUiDevice: ThermometerUiDevice) {
        viewModelScope.launch(Dispatchers.IO) {
            thermometerRepository.connectToDevice(context, this, thermometerUiDevice.address)
        }
    }
}
