package com.klewerro.mitemperaturenospyware.presentation.mainscreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klewerro.mitemperaturenospyware.R
import com.klewerro.mitemperaturenospyware.presentation.model.ConnectionStatus
import com.klewerro.mitemperaturenospyware.presentation.model.PermissionStatus
import com.klewerro.mitemperaturenospyware.presentation.model.ThermometerUiDevice
import com.klewerro.mitemperaturenospyware.presentation.util.UiText
import com.klewerro.temperatureSensor.ThermometerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
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
    private val connectedDevices = thermometerRepository.connectedDevices

    val devicesCombined = combine(
        scannedDevices,
        connectedDevices,
        thermometerRepository.connectingToDeviceAddress
    ) { scanned, connected, currentlyConnectingDeviceAddress ->
        val connectingUiDeviceIndex =
            scanned.indexOfFirst { it.address == currentlyConnectingDeviceAddress }
        scanned.mapIndexed { mapIndex, thermometerBleDevice ->
            val isConnected = connected.any { it.address == thermometerBleDevice.address }
            ThermometerUiDevice(
                name = thermometerBleDevice.name,
                address = thermometerBleDevice.address,
                rssi = thermometerBleDevice.rssi,
                connectionStatus = when {
                    connectingUiDeviceIndex > -1 && connectingUiDeviceIndex == mapIndex -> ConnectionStatus.CONNECTING
                    isConnected -> ConnectionStatus.CONNECTED
                    else -> ConnectionStatus.NOT_CONNECTED
                }
            )
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _uiTextError = Channel<UiText>()
    val uiTextError = _uiTextError.receiveAsFlow()

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

    fun connectToDevice(thermometerUiDevice: ThermometerUiDevice) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                thermometerRepository.connectToDevice(this, thermometerUiDevice.address)
            } catch (stateException: IllegalStateException) {
                _uiTextError.send(UiText.StringResource(R.string.already_connecting_to_different_device))
            }
        }
    }
}
