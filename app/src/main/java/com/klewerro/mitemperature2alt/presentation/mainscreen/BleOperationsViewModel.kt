package com.klewerro.mitemperature2alt.presentation.mainscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klewerro.mitemperature2alt.coreUi.R
import com.klewerro.mitemperature2alt.coreUi.util.UiText
import com.klewerro.mitemperature2alt.domain.usecase.ScanAndConnectToDeviceUseCase
import com.klewerro.mitemperature2alt.domain.usecase.thermometer.ThermometerListUseCase
import com.klewerro.mitemperature2alt.domain.util.DispatcherProvider
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
    thermometerListUseCase: ThermometerListUseCase,
    private val scanAndConnectToDeviceUseCase: ScanAndConnectToDeviceUseCase,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    private val _state = MutableStateFlow(BleOperationsState())
    val state = combine(
        _state,
        thermometerListUseCase()
    ) { stateValue, thermometers ->
        stateValue.copy(
            thermometers = thermometers
        )
    }
        .stateIn(viewModelScope, SharingStarted.Eagerly, BleOperationsState())
    fun onEvent(event: BleOperationsEvent) {
        when (event) {
            is BleOperationsEvent.ErrorConnectingToSavedThermometer -> _state.update {
                it.copy(
                    error = UiText.StringResource(R.string.thermometer_is_already_saved)
                )
            }
            is BleOperationsEvent.ConnectToDevice -> {
                changeThermometerOperationType(
                    ThermometerOperationType.ConnectingToDevice(event.thermometer.name)
                )
                viewModelScope.launch(dispatchers.io) {
                    scanAndConnectToDeviceUseCase(this, event.thermometer.address)
                        .onFailure {
                            _state.update {
                                it.copy(
                                    error = UiText.StringResource(
                                        R.string.unexpected_error_during_connecting_to_device
                                    )
                                )
                            }
                        }
                    changeThermometerOperationType(ThermometerOperationType.Idle)
                }
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

    private fun changeThermometerOperationType(operationType: ThermometerOperationType) {
        _state.update {
            it.copy(thermometerOperationType = operationType)
        }
    }
}
