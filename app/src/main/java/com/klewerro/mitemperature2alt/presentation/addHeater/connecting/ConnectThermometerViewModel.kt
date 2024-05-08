package com.klewerro.mitemperature2alt.presentation.addHeater.connecting

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klewerro.mitemperature2alt.R
import com.klewerro.mitemperature2alt.domain.usecase.thermometer.connect.ConnectToDeviceUseCase
import com.klewerro.mitemperature2alt.domain.usecase.thermometer.operations.ReadCurrentThermometerStatusUseCase
import com.klewerro.mitemperature2alt.presentation.navigation.Route
import com.klewerro.mitemperature2alt.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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
    private val readCurrentThermometerStatusUseCase: ReadCurrentThermometerStatusUseCase
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

    fun connectToDevice() {
        viewModelScope.launch(Dispatchers.IO) {
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

    fun changeThermometerName(name: String) {
        _state.update {
            it.copy(
                thermometerName = name
            )
        }
    }

    fun saveThermometer() {
    }
}
