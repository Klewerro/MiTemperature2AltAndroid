package com.klewerro.mitemperature2alt.presentation.addThermometer

import com.klewerro.mitemperature2alt.coreUi.util.UiText
import com.klewerro.mitemperature2alt.domain.model.ThermometerStatus

data class ConnectThermometerState(
    val thermometerAddress: String = "",
    val connectingStatus: ConnectingStatus = ConnectingStatus.NOT_CONNECTING,
    val error: UiText? = null,
    val connectThermometerStatus: ThermometerStatus? = null,
    val thermometerName: String = "",
    val thermometerSaved: Boolean = false
)

enum class ConnectingStatus {
    NOT_CONNECTING,
    CONNECTING,
    CONNECTED,
    ERROR
}
