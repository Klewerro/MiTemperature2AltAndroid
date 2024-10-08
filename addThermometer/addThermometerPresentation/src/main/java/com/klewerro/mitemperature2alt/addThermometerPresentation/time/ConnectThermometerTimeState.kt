package com.klewerro.mitemperature2alt.addThermometerPresentation.time

import com.klewerro.mitemperature2alt.core.util.LocalDateTimeUtils
import kotlinx.datetime.LocalDateTime

data class ConnectThermometerTimeState(
    val deviceDateTime: LocalDateTime = LocalDateTimeUtils.getCurrentUtcTime(),
    val thermometerDateTime: LocalDateTime? = null,
    val userPickedDateTime: LocalDateTime? = null,
    val selectedOption: Int = 0,
    val isTimePickerOpened: Boolean = false,
    val isDatePickerOpened: Boolean = false
)
