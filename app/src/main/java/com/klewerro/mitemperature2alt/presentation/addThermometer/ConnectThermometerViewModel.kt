package com.klewerro.mitemperature2alt.presentation.addThermometer

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klewerro.mitemperature2alt.R
import com.klewerro.mitemperature2alt.domain.usecase.thermometer.connect.ConnectToDeviceUseCase
import com.klewerro.mitemperature2alt.domain.usecase.thermometer.operations.ReadCurrentThermometerStatusUseCase
import com.klewerro.mitemperature2alt.domain.usecase.thermometer.persistence.SaveThermometerUseCase
import com.klewerro.mitemperature2alt.domain.util.DispatcherProvider
import com.klewerro.mitemperature2alt.presentation.navigation.Route
import com.klewerro.mitemperature2alt.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ConnectThermometerViewModel @Inject constructor(
    savedState: SavedStateHandle,
    private val connectToDeviceUseCase: ConnectToDeviceUseCase,
    private val readCurrentThermometerStatusUseCase: ReadCurrentThermometerStatusUseCase,
    private val saveThermometerUseCase: SaveThermometerUseCase,
    private val dispatchers: DispatcherProvider
) : ViewModel() {
    private val _state = MutableStateFlow(ConnectThermometerState())
    val state = combine(
        _state,
        savedState.getStateFlow(Route.ConnectDeviceRoutes.PARAM_ADDRESS, "")
    ) { stateValue, address ->
        stateValue.copy(
            thermometerAddress = address
        )
    }
        .stateIn(viewModelScope, SharingStarted.Eagerly, ConnectThermometerState())

    fun onEvent(event: ConnectThermometerEvent) {
        when (event) {
            ConnectThermometerEvent.ConnectToDevice -> handleConnectToDevice()
            is ConnectThermometerEvent.ChangeThermometerName ->
                handleChangeThermometerName(event.name)
            ConnectThermometerEvent.SaveThermometer -> handleSaveThermometer()
        }
    }

    private fun handleConnectToDevice() {
        viewModelScope.launch(dispatchers.io) {
            try {
                _state.update {
                    it.copy(
                        connectingStatus = ConnectingStatus.CONNECTING
                    )
                }
                with(state.value.thermometerAddress) {
                    connectToDeviceUseCase(this@launch, this)
                    val currentStatus = readCurrentThermometerStatusUseCase(this)
                    _state.update {
                        it.copy(
                            connectingStatus = ConnectingStatus.CONNECTED,
                            connectThermometerStatus = currentStatus
                        )
                    }
                }
            } catch (stateException: IllegalStateException) {
                Timber.e(stateException, "connectToDevice exception: ${stateException.message}")
                _state.update {
                    it.copy(
                        connectingStatus = ConnectingStatus.ERROR,
                        error = UiText.StringResource(
                            R.string.unexpected_error_during_connecting_to_device
                        )
                    )
                }
            }
        }
    }

    private fun handleChangeThermometerName(name: String) {
        _state.update {
            it.copy(
                error = if (name.isNotBlank()) null else it.error,
                thermometerName = name
            )
        }
    }

    private fun handleSaveThermometer() {
        val thermometerName = state.value.thermometerName
        if (thermometerName.isBlank()) {
            _state.update {
                it.copy(
                    error = UiText.StringResource(R.string.thermometer_name_must_not_be_empty)
                )
            }
            return
        }

        viewModelScope.launch(dispatchers.io) {
            saveThermometerUseCase(state.value.thermometerAddress, thermometerName)
            _state.update {
                it.copy(
                    thermometerSaved = true
                )
            }
        }
    }
}