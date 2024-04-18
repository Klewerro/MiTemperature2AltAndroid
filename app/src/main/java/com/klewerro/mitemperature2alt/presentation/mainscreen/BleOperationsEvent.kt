package com.klewerro.mitemperature2alt.presentation.mainscreen

import com.klewerro.mitemperature2alt.domain.model.ThermometerScanResult

sealed class BleOperationsEvent {
    data class ConnectToDevice(val thermometerDevice: ThermometerScanResult) : BleOperationsEvent()
    data class GetStatusForDevice(val address: String) : BleOperationsEvent()
    data class SubscribeForDeviceStatusUpdates(val address: String) : BleOperationsEvent()
    data class OpenSaveThermometer(val address: String) : BleOperationsEvent()
    data object CloseSaveThermometer : BleOperationsEvent()
    data class SaveThermometer(val name: String) : BleOperationsEvent()
    data object ErrorDismissed : BleOperationsEvent()
}