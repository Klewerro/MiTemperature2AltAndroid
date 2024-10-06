package com.klewerro.mitemperature2alt.thermometerDetails.presentation

import com.klewerro.mitemperature2alt.domain.model.HourlyRecord
import com.klewerro.mitemperature2alt.domain.model.SavedThermometer
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class ThermometerDetailsState(
    val thermometer: SavedThermometer?,
    val hourlyRecords: List<HourlyRecord> = emptyList(),
    val selectedDate: LocalDate = Clock.System.now().toLocalDateTime(
        TimeZone.currentSystemDefault()
    ).date
)
