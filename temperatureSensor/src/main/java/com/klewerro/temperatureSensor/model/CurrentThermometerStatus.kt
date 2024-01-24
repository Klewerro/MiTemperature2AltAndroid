package com.klewerro.temperatureSensor.model

data class CurrentThermometerStatus(
    val temperature: Float,
    val humidity: Int,
    val voltage: Float
)
