package com.klewerro.mitemperature2alt.addThermometerPresentation.time

import com.klewerro.mitemperature2alt.core.util.LocalDateTimeUtils
import kotlinx.datetime.LocalDateTime

data class ConnectThermometerTimeState(
    val deviceDateTime: LocalDateTime = LocalDateTimeUtils.getCurrentUtcTime(),
    val thermometerDateTime: LocalDateTime? = null,
    val userProvidedDateTime: LocalDateTime? = null,
    val dateTimeType: DateTimeType = DateTimeType.THERMOMETER,
    val isTimePickerOpened: Boolean = false,
    val isDatePickerOpened: Boolean = false,
    val sendingTime: Boolean = false
)
