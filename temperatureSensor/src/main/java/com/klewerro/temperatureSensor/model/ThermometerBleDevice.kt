package com.klewerro.temperatureSensor.model

data class ThermometerBleDevice(
    val name: String,
    val address: String,
    val rssi: Int
)
