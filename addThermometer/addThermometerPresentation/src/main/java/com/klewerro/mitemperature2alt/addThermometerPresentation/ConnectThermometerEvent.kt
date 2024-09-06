package com.klewerro.mitemperature2alt.addThermometerPresentation

import kotlinx.coroutines.CoroutineScope

sealed class ConnectThermometerEvent {
    data class ConnectToDevice(val bleOperationsViewModelScope: CoroutineScope) :
        ConnectThermometerEvent()

    data class ChangeThermometerName(val name: String) : ConnectThermometerEvent()
    data object SaveThermometer : ConnectThermometerEvent()
}
