package com.klewerro.mitemperature2alt.domain.model

data class Thermometer(
    val name: String,
    val address: String,
    val temperature: Float,
    val humidity: Int,
    val voltage: Float,
    val rssi: RssiStrength,
    val thermometerConnectionStatus: ThermometerConnectionStatus
)
