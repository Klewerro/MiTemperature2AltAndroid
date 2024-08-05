package com.klewerro.mitemperature2alt.presentation.mainscreen

sealed class ThermometerOperationType {
    data object Idle : ThermometerOperationType()
    data class ConnectingToDevice(val thermometerName: String) : ThermometerOperationType()
}
