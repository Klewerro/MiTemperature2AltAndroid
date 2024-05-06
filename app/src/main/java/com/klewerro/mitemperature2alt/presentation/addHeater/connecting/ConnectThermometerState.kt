package com.klewerro.mitemperature2alt.presentation.addHeater.connecting

import com.klewerro.mitemperature2alt.domain.model.ThermometerStatus
import com.klewerro.mitemperature2alt.presentation.util.UiText

data class ConnectThermometerState(
    val thermometerAddress: String = "",
    val isConnecting: Boolean = false,
    val error: UiText? = null,
    val isConnected: Boolean = false,
    val connectThermometerStatus: ThermometerStatus? = null
)
