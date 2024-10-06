package com.klewerro.mitemperature2alt.thermometerDetails.presentation

import com.klewerro.mitemperature2alt.domain.model.HourlyRecord
import com.klewerro.mitemperature2alt.domain.model.SavedThermometer

data class ThermometerDetailsState(
    val thermometer: SavedThermometer?,
    val hourlyRecords: List<HourlyRecord> = emptyList()
)
