package com.klewerro.mitemperature2alt.domain.model

import kotlinx.datetime.LocalDateTime

data class HourlyRecord(
    val index: Int,
    val dateTime: LocalDateTime,
    val temperatureMin: Float,
    val temperatureMax: Float,
    val humidityMin: Int,
    val humidityMax: Int
)
