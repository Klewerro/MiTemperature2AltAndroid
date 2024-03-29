package com.klewerro.mitemperature2alt.presentation.addHeater

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klewerro.mitemperature2alt.domain.usecase.thermometer.scan.IsScanningForDevicesUseCase
import com.klewerro.mitemperature2alt.domain.usecase.thermometer.scan.ScanForDevicesUseCase
import com.klewerro.mitemperature2alt.domain.usecase.thermometer.scan.SearchedDevicesUseCase
import com.klewerro.mitemperature2alt.domain.util.DispatcherProvider
import com.klewerro.mitemperature2alt.presentation.model.PermissionStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeviceSearchViewModel @Inject constructor(
    searchedDevicesUseCase: SearchedDevicesUseCase,
    isScanningForDevicesUseCase: IsScanningForDevicesUseCase,
    private val scanForDevicesUseCase: ScanForDevicesUseCase,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    private var scanningBleDevicesJob: Job? = null

    val isScanningForDevices = isScanningForDevicesUseCase()
    val scannerDevices = searchedDevicesUseCase()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    var permissionGrantStatus by mutableStateOf(PermissionStatus.DECLINED)

    fun scanForDevices() {
        if (scanningBleDevicesJob != null) {
            stopScanForDevices()
        }
        viewModelScope.launch(dispatchers.io) {
            scanningBleDevicesJob = scanForDevicesUseCase(this)
        }
    }

    fun stopScanForDevices() {
        scanningBleDevicesJob?.cancel()
        scanningBleDevicesJob = null
    }
}
