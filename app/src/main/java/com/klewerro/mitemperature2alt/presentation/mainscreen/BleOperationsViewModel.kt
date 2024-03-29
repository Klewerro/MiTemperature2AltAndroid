package com.klewerro.mitemperature2alt.presentation.mainscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klewerro.mitemperature2alt.R
import com.klewerro.mitemperature2alt.domain.model.ThermometerScanResult
import com.klewerro.mitemperature2alt.domain.usecase.thermometer.connect.ConnectToDeviceUseCase
import com.klewerro.mitemperature2alt.domain.usecase.thermometer.connect.ConnectedDevicesUseCase
import com.klewerro.mitemperature2alt.domain.usecase.thermometer.operations.ReadCurrentThermometerStatusUseCase
import com.klewerro.mitemperature2alt.domain.usecase.thermometer.operations.SubscribeToCurrentThermometerStatusUseCase
import com.klewerro.mitemperature2alt.domain.usecase.thermometer.persistence.SaveThermometerUseCase
import com.klewerro.mitemperature2alt.domain.usecase.thermometer.persistence.SavedThermometersUseCase
import com.klewerro.mitemperature2alt.domain.util.DispatcherProvider
import com.klewerro.mitemperature2alt.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BleOperationsViewModel @Inject constructor(
    connectedDevicesUseCase: ConnectedDevicesUseCase,
    savedThermometersUseCase: SavedThermometersUseCase,
    private val connectToDeviceUseCase: ConnectToDeviceUseCase,
    private val readCurrentThermometerStatusUseCase: ReadCurrentThermometerStatusUseCase,
    private val subscribeToCurrentThermometerStatusUseCase:
        SubscribeToCurrentThermometerStatusUseCase,
    private val saveThermometerUseCase: SaveThermometerUseCase,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    // Todo: Create viewState class
    //  MVI events

    private val _uiTextError = Channel<UiText>()
    val uiTextError = _uiTextError.receiveAsFlow()

    val connectedDevices = connectedDevicesUseCase()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val savedThermometers = savedThermometersUseCase()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun connectToDevice(thermometerDevice: ThermometerScanResult) {
        viewModelScope.launch(dispatchers.io) {
            try {
                connectToDeviceUseCase(this, thermometerDevice.address)
            } catch (stateException: IllegalStateException) {
                Log.d(
                    "BleOperationsViewModel",
                    "connectToDevice exception: ${stateException.message}"
                )
                stateException.printStackTrace()
                _uiTextError.send(
                    UiText.StringResource(R.string.already_connecting_to_different_device)
                )
            }
        }
    }

    fun getStatusForDevice(address: String) {
        viewModelScope.launch(dispatchers.io) {
            readCurrentThermometerStatusUseCase(address)
        }
    }

    fun subscribeForDeviceStatusUpdates(address: String) {
        viewModelScope.launch(dispatchers.io) {
            subscribeToCurrentThermometerStatusUseCase(address, this)
        }
    }

    fun saveThermometer(address: String) {
        viewModelScope.launch(dispatchers.io) {
            saveThermometerUseCase(address, "Thermometer name 1")
        }
    }
}
