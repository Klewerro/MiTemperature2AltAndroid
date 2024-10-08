package com.klewerro.mitemperature2alt.addThermometerPresentation.time

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

sealed class ConnectThermometerTimeEvent {
    data class SelectedOptionChanged(val dateTimeType: DateTimeType) : ConnectThermometerTimeEvent()
    data object OpenDatePicker : ConnectThermometerTimeEvent()
    data object CloseDatePicker : ConnectThermometerTimeEvent()
    data object CloseTimePicker : ConnectThermometerTimeEvent()
    data class DatePicked(val date: LocalDate) : ConnectThermometerTimeEvent()
    data class TimePicked(val time: LocalTime) : ConnectThermometerTimeEvent()
    data object SendTimeToThermometer : ConnectThermometerTimeEvent()
}
