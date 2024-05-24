package com.klewerro.mitemperature2alt.presentation.addThermometer

sealed class ConnectThermometerEvent {
    data object ConnectToDevice : ConnectThermometerEvent()
    data class ChangeThermometerName(val name: String) : ConnectThermometerEvent()
    data object SaveThermometer : ConnectThermometerEvent()
}
