package com.klewerro.mitemperature2alt.domain.model

data class ThermometerScanResult(
    val name: String,
    val address: String,
    val rssi: Int?,
    val scannedDeviceStatus: ScannedDeviceStatus
)
