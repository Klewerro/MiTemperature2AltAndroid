package com.klewerro.mitemperature2alt.thermometerDetails.presentation

import com.klewerro.mitemperature2alt.core.util.LocalDateTimeUtils.convertEpochMillisToLocalDate

sealed class ThermometerDetailsEvent {
    data object OnPreviousDaySelected : ThermometerDetailsEvent()
    data object OnNextDaySelected : ThermometerDetailsEvent()
    data object DatePickerOpened : ThermometerDetailsEvent()
    data object DatePickerDismissed : ThermometerDetailsEvent()
    data class DatePickerDateSelected(val selectedDateMillis: Long) : ThermometerDetailsEvent() {
        val selectedDateLocalDate = selectedDateMillis.convertEpochMillisToLocalDate()
    }
}
