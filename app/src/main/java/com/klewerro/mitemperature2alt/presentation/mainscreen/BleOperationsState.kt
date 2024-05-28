package com.klewerro.mitemperature2alt.presentation.mainscreen

import com.klewerro.mitemperature2alt.coreUi.util.UiText
import com.klewerro.mitemperature2alt.domain.model.Thermometer

data class BleOperationsState(
    val thermometers: List<Thermometer> = emptyList(),
    val error: UiText? = null
)
