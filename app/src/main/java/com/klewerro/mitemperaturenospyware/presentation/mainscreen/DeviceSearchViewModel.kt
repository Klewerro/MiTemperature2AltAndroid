package com.klewerro.mitemperaturenospyware.presentation.mainscreen

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klewerro.mitemperaturenospyware.presentation.model.PermissionStatus
import com.klewerro.temperatureSensor.ThermometerDevicesBleScanner
import com.klewerro.temperatureSensor.model.ThermometerBleDevice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class DeviceSearchViewModel : ViewModel() {

    private val scanner = ThermometerDevicesBleScanner()
    private var scanningBleDevicesJob: Job? = null

    val scannedDevices = scanner.bleDevices
    val isScanningForDevices = scanner.isScanning

    var permissionGrantStatus by mutableStateOf(PermissionStatus.DECLINED)

    fun scanForDevices(context: Context) {
        if (scanningBleDevicesJob != null) {
            stopScanForDevices()
        }
        viewModelScope.launch(Dispatchers.IO) {
            scanningBleDevicesJob = scanner.scanForDevices(context, this)
        }
    }

    fun stopScanForDevices() {
        scanningBleDevicesJob?.cancel()
        scanningBleDevicesJob = null
    }

    fun connectToDevice(context: Context, thermometerBleDevice: ThermometerBleDevice) {
        viewModelScope.launch(Dispatchers.IO) {
            scanner.connectToDevice(context, this, thermometerBleDevice)
        }
    }
}
