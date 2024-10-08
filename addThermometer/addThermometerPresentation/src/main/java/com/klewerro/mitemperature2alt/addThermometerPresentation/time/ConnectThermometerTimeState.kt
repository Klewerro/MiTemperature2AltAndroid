package com.klewerro.mitemperature2alt.addThermometerPresentation.time

import com.klewerro.mitemperature2alt.core.util.LocalDateTimeUtils
import kotlinx.datetime.LocalDateTime

data class ConnectThermometerTimeState(
    val deviceTime: LocalDateTime = LocalDateTimeUtils.getCurrentUtcTime(),
    val thermometerTime: LocalDateTime? = null,
    val selectedOption: Int = 0
)
