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
    private val connectToDeviceUseCase: ConnectToDeviceUseCase,
    private val readCurrentThermometerStatusUseCase: ReadCurrentThermometerStatusUseCase,
    private val subscribeToCurrentThermometerStatusUseCase:
        SubscribeToCurrentThermometerStatusUseCase,
    private val saveThermometerUseCase: SaveThermometerUseCase,
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
            is BleOperationsEvent.ConnectToDevice -> handleConnectToDevice(event.thermometerDevice)
            is BleOperationsEvent.GetStatusForDevice -> handleGetStatusForDevice(event.address)
            is BleOperationsEvent.SubscribeForDeviceStatusUpdates ->
                handleSubscribeForDeviceStatusUpdates(event.address)
            is BleOperationsEvent.SaveThermometer ->
                handleSaveThermometer(event.address)
            BleOperationsEvent.ErrorDismissed -> {
                _state.update {
                    it.copy(
                        error = null
                    )
                }
            }
        }
    }

    private fun handleConnectToDevice(thermometerDevice: ThermometerScanResult) {
        viewModelScope.launch(dispatchers.io) {
            try {
                connectToDeviceUseCase(this, thermometerDevice.address)
            } catch (stateException: IllegalStateException) {
                Log.d(
                    "BleOperationsViewModel",
                    "connectToDevice exception: ${stateException.message}"
                )
                stateException.printStackTrace()
                _state.update {
                    it.copy(
                        error = UiText.StringResource(
                            R.string.already_connecting_to_different_device
                        )
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

    private fun handleSaveThermometer(address: String) {
        viewModelScope.launch(dispatchers.io) {
            saveThermometerUseCase(address, "Thermometer name 1")
        }
    }
}
