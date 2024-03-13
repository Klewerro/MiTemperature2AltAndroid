package com.klewerro.mitemperature2alt.domain.model

data class ThermometerDevice(
    val address: String,
    val name: String,
    val temperature: Float,
    val humidity: Int,
    val voltage: Float,
    val rssi: Int?
)
