package com.klewerro.mitemperature2alt.presentation.mainscreen

import com.klewerro.mitemperature2alt.domain.model.Thermometer

sealed class BleOperationsEvent {
    data class ConnectToDevice(val thermometer: Thermometer) : BleOperationsEvent()
    data class ErrorConnectingToSavedThermometer(val name: String) : BleOperationsEvent()
    data object ErrorDismissed : BleOperationsEvent()
    data object CancelHourlyRecordsSync : BleOperationsEvent()
}
