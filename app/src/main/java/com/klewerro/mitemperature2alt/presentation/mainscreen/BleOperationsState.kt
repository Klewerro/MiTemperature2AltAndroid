package com.klewerro.mitemperature2alt.presentation.mainscreen

import com.klewerro.mitemperature2alt.domain.model.SavedThermometer
import com.klewerro.mitemperature2alt.domain.model.ThermometerDevice
import com.klewerro.mitemperature2alt.presentation.util.UiText

data class BleOperationsState(
    val connectedDevices: List<ThermometerDevice> = emptyList(),
    val savedThermometers: List<SavedThermometer> = emptyList(),
    val error: UiText? = null
)
