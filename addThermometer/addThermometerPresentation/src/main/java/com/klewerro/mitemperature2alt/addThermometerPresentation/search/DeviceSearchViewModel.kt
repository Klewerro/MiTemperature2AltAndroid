package com.klewerro.mitemperature2alt.addThermometerPresentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klewerro.mitemperature2alt.addThermometerDomain.usecase.scan.IsScanningForDevicesUseCase
import com.klewerro.mitemperature2alt.addThermometerDomain.usecase.scan.ScanForDevicesUseCase
import com.klewerro.mitemperature2alt.addThermometerDomain.usecase.scan.SearchedDevicesUseCase
import com.klewerro.mitemperature2alt.coreUi.R
import com.klewerro.mitemperature2alt.coreUi.util.UiText
import com.klewerro.mitemperature2alt.domain.util.DispatcherProvider
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

            is DeviceSearchEvent.ErrorConnectingToSavedThermometer -> _state.update {
                it.copy(
                    error = UiText.StringResource(R.string.thermometer_is_already_saved)
                )
            }
            DeviceSearchEvent.ErrorDismissed -> _state.update {
                it.copy(
                    error = null
                )
            }
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
