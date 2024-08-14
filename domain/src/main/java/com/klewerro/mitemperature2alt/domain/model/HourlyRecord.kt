package com.klewerro.mitemperature2alt.domain.model

data class HourlyRecord(
    val index: Int,
    val time: Int,
    val temperatureMin: Float,
    val temperatureMax: Float,
    val humidityMin: Int,
    val humidityMax: Int
)
