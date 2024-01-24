package com.klewerro.mitemperaturenospyware.presentation.mainscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klewerro.mitemperaturenospyware.R
import com.klewerro.mitemperaturenospyware.presentation.model.ConnectionStatus
import com.klewerro.mitemperaturenospyware.presentation.model.ThermometerUiDevice
import com.klewerro.mitemperaturenospyware.presentation.util.UiText
import com.klewerro.temperatureSensor.ThermometerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BleOperationsViewModel @Inject constructor(
    private val thermometerRepository: ThermometerRepository
) : ViewModel() {

    private val _uiTextError = Channel<UiText>()
    val uiTextError = _uiTextError.receiveAsFlow()

    val connectedDevices = thermometerRepository.connectedDevices // Todo: Convert to UiBleDevice after creating useCases
        .map {
            it.map { thermometerBleDevice ->
                ThermometerUiDevice(
                    name = thermometerBleDevice.name,
                    address = thermometerBleDevice.address,
                    rssi = thermometerBleDevice.rssi,
                    connectionStatus = ConnectionStatus.CONNECTED,
                    status = thermometerBleDevice.status
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun connectToDevice(thermometerUiDevice: ThermometerUiDevice) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                thermometerRepository.connectToDevice(this, thermometerUiDevice.address)
            } catch (stateException: IllegalStateException) {
                _uiTextError.send(UiText.StringResource(R.string.already_connecting_to_different_device))
            }
        }
    }
}
