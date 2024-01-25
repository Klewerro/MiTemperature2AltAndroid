package com.klewerro.mitemperaturenospyware.domain.model

data class CurrentThermometerStatus(
    val temperature: Float,
    val humidity: Int,
    val voltage: Float
)
