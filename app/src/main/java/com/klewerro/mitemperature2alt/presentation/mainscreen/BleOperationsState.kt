package com.klewerro.mitemperature2alt.presentation.mainscreen

import com.klewerro.mitemperature2alt.coreUi.util.UiText
import com.klewerro.mitemperature2alt.domain.model.SavedThermometer
import com.klewerro.mitemperature2alt.domain.model.ThermometerDevice

data class BleOperationsState(
    val connectedDevices: List<ThermometerDevice> = emptyList(),
    val savedThermometers: List<SavedThermometer> = emptyList(),
    val isShowingSaveDialog: Boolean = false,
    val error: UiText? = null
)
