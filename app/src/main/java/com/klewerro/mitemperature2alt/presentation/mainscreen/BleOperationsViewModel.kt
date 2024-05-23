package com.klewerro.mitemperature2alt.presentation.mainscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klewerro.mitemperature2alt.coreUi.R
import com.klewerro.mitemperature2alt.domain.usecase.thermometer.ConnectedDevicesUseCase
import com.klewerro.mitemperature2alt.domain.usecase.thermometer.operations.ReadCurrentThermometerStatusUseCase
import com.klewerro.mitemperature2alt.domain.usecase.thermometer.operations.SubscribeToCurrentThermometerStatusUseCase
import com.klewerro.mitemperature2alt.domain.usecase.thermometer.persistence.SavedThermometersUseCase
import com.klewerro.mitemperature2alt.domain.util.DispatcherProvider
import com.klewerro.mitemperature2alt.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BleOperationsViewModel @Inject constructor(
    connectedDevicesUseCase: ConnectedDevicesUseCase,
    savedThermometersUseCase: SavedThermometersUseCase,
    private val readCurrentThermometerStatusUseCase: ReadCurrentThermometerStatusUseCase,
    private val subscribeToCurrentThermometerStatusUseCase:
        SubscribeToCurrentThermometerStatusUseCase,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    private val _state = MutableStateFlow(BleOperationsState())
    val state = combine(
        _state,
        connectedDevicesUseCase(),
        savedThermometersUseCase()
    ) { stateValue, connectedDevices, savedThermometers ->
        stateValue.copy(
            connectedDevices = connectedDevices,
            savedThermometers = savedThermometers
        )
    }
        .stateIn(viewModelScope, SharingStarted.Eagerly, BleOperationsState())

    fun onEvent(event: BleOperationsEvent) {
        when (event) {
            is BleOperationsEvent.GetStatusForDevice -> handleGetStatusForDevice(event.address)
            is BleOperationsEvent.SubscribeForDeviceStatusUpdates ->
                handleSubscribeForDeviceStatusUpdates(event.address)
            is BleOperationsEvent.ErrorConnectingToSavedThermometer -> _state.update {
                it.copy(
                    error = UiText.StringResource(R.string.thermometer_is_already_saved)
                )
            }
            BleOperationsEvent.ErrorDismissed -> {
                _state.update {
                    it.copy(
                        error = null
                    )
                }
            }
        }
    }

    private fun handleGetStatusForDevice(address: String) {
        viewModelScope.launch(dispatchers.io) {
            readCurrentThermometerStatusUseCase(address)
        }
    }

    private fun handleSubscribeForDeviceStatusUpdates(address: String) {
        viewModelScope.launch(dispatchers.io) {
            subscribeToCurrentThermometerStatusUseCase(address, this)
        }
    }
}
