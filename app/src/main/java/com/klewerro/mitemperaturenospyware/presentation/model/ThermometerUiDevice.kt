package com.klewerro.mitemperaturenospyware.presentation.model

import com.klewerro.temperatureSensor.model.CurrentThermometerStatus

data class ThermometerUiDevice(
    val name: String,
    val address: String,
    val rssi: Int,
    val connectionStatus: ConnectionStatus,
    val status: CurrentThermometerStatus?
)

enum class ConnectionStatus {
    NOT_CONNECTED,
    CONNECTING,
    CONNECTED
}
