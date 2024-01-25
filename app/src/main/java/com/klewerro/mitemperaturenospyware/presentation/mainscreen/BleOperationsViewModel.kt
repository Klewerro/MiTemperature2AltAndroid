package com.klewerro.mitemperaturenospyware.presentation.mainscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klewerro.mitemperaturenospyware.R
import com.klewerro.mitemperaturenospyware.domain.model.ConnectionStatus
import com.klewerro.mitemperaturenospyware.domain.model.ThermometerDevice
import com.klewerro.mitemperaturenospyware.domain.usecase.thermometer.connect.ConnectToDeviceUseCase
import com.klewerro.mitemperaturenospyware.domain.usecase.thermometer.connect.ConnectedDevicesUseCase
import com.klewerro.mitemperaturenospyware.domain.usecase.thermometer.operations.ReadCurrentThermometerStatusUseCase
import com.klewerro.mitemperaturenospyware.domain.usecase.thermometer.operations.SubscribeToCurrentThermometerStatusUseCase
import com.klewerro.mitemperaturenospyware.presentation.util.UiText
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
    connectedDevicesUseCase: ConnectedDevicesUseCase,
    private val connectToDeviceUseCase: ConnectToDeviceUseCase,
    private val readCurrentThermometerStatusUseCase: ReadCurrentThermometerStatusUseCase,
    private val subscribeToCurrentThermometerStatusUseCase: SubscribeToCurrentThermometerStatusUseCase
) : ViewModel() {

    private val _uiTextError = Channel<UiText>()
    val uiTextError = _uiTextError.receiveAsFlow()

    val connectedDevices = connectedDevicesUseCase() // Todo: Convert to UiBleDevice after creating useCases
        .map {
            it.map { thermometerBleDevice ->
                ThermometerDevice(
                    name = thermometerBleDevice.name,
                    address = thermometerBleDevice.address,
                    rssi = thermometerBleDevice.rssi,
                    connectionStatus = ConnectionStatus.CONNECTED,
                    status = thermometerBleDevice.status
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun connectToDevice(thermometerDevice: ThermometerDevice) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                connectToDeviceUseCase(this, thermometerDevice.address)
            } catch (stateException: IllegalStateException) {
                _uiTextError.send(UiText.StringResource(R.string.already_connecting_to_different_device))
            }
        }
    }

    fun getStatusForDevice(address: String) {
        viewModelScope.launch(Dispatchers.IO) {
            readCurrentThermometerStatusUseCase(address)
        }
    }

    fun subscribeForDeviceStatusUpdates(address: String) {
        viewModelScope.launch(Dispatchers.IO) {
            subscribeToCurrentThermometerStatusUseCase(address, this)
        }
    }
}
