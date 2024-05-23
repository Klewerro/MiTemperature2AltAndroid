package com.klewerro.mitemperature2alt.addThermometerPresentation.search

import com.klewerro.mitemperature2alt.coreUi.util.UiText
import com.klewerro.mitemperature2alt.domain.model.ThermometerScanResult

data class DeviceSearchState(
    val isScanningForDevices: Boolean = false,
    val scannedDevices: List<ThermometerScanResult> = emptyList(),
    val permissionGrantStatus: PermissionStatus = PermissionStatus.DECLINED,
    val error: UiText? = null
)
