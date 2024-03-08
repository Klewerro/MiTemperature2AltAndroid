package com.klewerro.mitemperaturenospyware.domain.model

data class ThermometerScanResult(
    val name: String,
    val address: String,
    val rssi: Int?,
    val connectionStatus: ConnectionStatus
)
