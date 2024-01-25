package com.klewerro.mitemperaturenospyware.domain.model

data class ThermometerDevice(
    val name: String,
    val address: String,
    val rssi: Int,
    val connectionStatus: ConnectionStatus,
    val status: CurrentThermometerStatus?
)
