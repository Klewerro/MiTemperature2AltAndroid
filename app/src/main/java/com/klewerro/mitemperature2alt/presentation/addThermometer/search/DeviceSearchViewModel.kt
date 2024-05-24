package com.klewerro.mitemperature2alt.presentation.addThermometer.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klewerro.mitemperature2alt.domain.usecase.thermometer.scan.IsScanningForDevicesUseCase
import com.klewerro.mitemperature2alt.domain.usecase.thermometer.scan.ScanForDevicesUseCase
import com.klewerro.mitemperature2alt.domain.usecase.thermometer.scan.SearchedDevicesUseCase
import com.klewerro.mitemperature2alt.domain.util.DispatcherProvider
import com.klewerro.mitemperature2alt.presentation.model.PermissionStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeviceSearchViewModel @Inject constructor(
    val searchedDevicesUseCase: SearchedDevicesUseCase,
    val isScanningForDevicesUseCase: IsScanningForDevicesUseCase,
    private val scanForDevicesUseCase: ScanForDevicesUseCase,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    private var scanningBleDevicesJob: Job? = null
    private var isScanStoppedByUser: Boolean = false

    private val _state = MutableStateFlow(DeviceSearchState())
    val state = combine(
        _state,
        isScanningForDevicesUseCase(),
        searchedDevicesUseCase()
    ) { stateValue, isScanning, searchedDevices ->
        stateValue.copy(
            isScanningForDevices = isScanning,
            scannedDevices = searchedDevices
        )
    }
        .stateIn(viewModelScope, SharingStarted.Eagerly, DeviceSearchState())

    fun onEvent(event: DeviceSearchEvent) {
        when (event) {
            is DeviceSearchEvent.ScanForDevices -> handleScanForDevices(event.byUser)
            is DeviceSearchEvent.StopScanForDevices -> handleStopScanForDevices(event.byUser)
            is DeviceSearchEvent.UpdatePermissionStatus -> handlePermissionChange(
                event.permissionStatus
            )
        }
    }

    private fun handleScanForDevices(startedByUser: Boolean) {
        if (scanningBleDevicesJob != null) {
            handleStopScanForDevices(startedByUser)
        }
        if (startedByUser) {
            this.isScanStoppedByUser = false
        }
        if (!isScanStoppedByUser) {
            viewModelScope.launch(dispatchers.io) {
                scanningBleDevicesJob = scanForDevicesUseCase(this)
            }
        }
    }

    private fun handleStopScanForDevices(stoppedByUser: Boolean) {
        if (stoppedByUser) {
            this.isScanStoppedByUser = true
        }
        scanningBleDevicesJob?.cancel()
        scanningBleDevicesJob = null
    }

    private fun handlePermissionChange(permissionStatus: PermissionStatus) {
        _state.update {
            it.copy(
                permissionGrantStatus = permissionStatus
            )
        }
    }
}
