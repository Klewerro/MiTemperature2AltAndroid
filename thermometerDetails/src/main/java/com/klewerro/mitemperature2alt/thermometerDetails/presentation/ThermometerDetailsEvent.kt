package com.klewerro.mitemperature2alt.thermometerDetails.presentation

sealed class ThermometerDetailsEvent {
    data object OnPreviousDaySelected : ThermometerDetailsEvent()
    data object OnNextDaySelected : ThermometerDetailsEvent()
}
