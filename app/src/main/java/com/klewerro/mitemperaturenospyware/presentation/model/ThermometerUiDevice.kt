package com.klewerro.mitemperaturenospyware.presentation.model

data class ThermometerUiDevice(
    val name: String,
    val address: String,
    val rssi: Int,
    val connectionStatus: ConnectionStatus
)

enum class ConnectionStatus {
    NOT_CONNECTED,
    CONNECTING,
    CONNECTED
}
