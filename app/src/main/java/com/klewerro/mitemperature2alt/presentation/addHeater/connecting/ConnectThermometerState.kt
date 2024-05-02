package com.klewerro.mitemperature2alt.presentation.addHeater.connecting

import com.klewerro.mitemperature2alt.domain.model.ConnectionStatus

data class ConnectThermometerState(
    val connectionStatus: ConnectionStatus = ConnectionStatus.NOT_CONNECTED
)
