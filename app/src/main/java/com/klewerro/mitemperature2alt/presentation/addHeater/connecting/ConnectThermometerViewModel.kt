package com.klewerro.mitemperature2alt.presentation.addHeater.connecting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klewerro.mitemperature2alt.domain.model.ConnectionStatus
import com.klewerro.mitemperature2alt.domain.usecase.thermometer.connect.ConnectToDeviceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class ConnectThermometerViewModel @Inject constructor(
    private val connectToDeviceUseCase: ConnectToDeviceUseCase
) : ViewModel() {
    var saveThermometerAddress: String? = null

    private val _state = MutableStateFlow(ConnectThermometerState())
    val state = _state.asStateFlow()

    fun connectToDevice() {
        saveThermometerAddress?.let { saveThermometerAddressValue ->
            _state.update {
                it.copy(
                    connectionStatus = ConnectionStatus.CONNECTING // Todo: Temporary - later fetch repository status using useCase
                )
            }
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    connectToDeviceUseCase(this, saveThermometerAddressValue)
                    _state.update {
                        it.copy(
                            connectionStatus = ConnectionStatus.CONNECTED // Todo: Temporary - later fetch repository status using useCase
                        )
                    }
                } catch (stateException: IllegalStateException) {
                    Timber.d("connectToDevice exception: ${stateException.message}")
                    stateException.printStackTrace()
//                _state.update {
//                    it.copy(
//                        error = UiText.StringResource(
//                            R.string.already_connecting_to_different_device
//                        )
//                    )
//                }
                }
            }
        } ?: run {
            // Todo: Error catching
        }
    }
}
