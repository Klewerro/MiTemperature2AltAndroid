package com.klewerro.mitemperature2alt.presentation.mainscreen

sealed class BleOperationsEvent {
    data class GetStatusForDevice(val address: String) : BleOperationsEvent()
    data class SubscribeForDeviceStatusUpdates(val address: String) : BleOperationsEvent()
    data class ErrorConnectingToSavedThermometer(val name: String) : BleOperationsEvent()
    data class ConnectToDevice(val address: String) : BleOperationsEvent()
    data object ErrorDismissed : BleOperationsEvent()
}
