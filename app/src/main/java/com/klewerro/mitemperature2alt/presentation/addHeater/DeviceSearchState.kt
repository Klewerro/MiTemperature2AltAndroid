package com.klewerro.mitemperature2alt.presentation.addHeater

import com.klewerro.mitemperature2alt.domain.model.ThermometerScanResult
import com.klewerro.mitemperature2alt.presentation.model.PermissionStatus

data class DeviceSearchState(
    val isScanningForDevices: Boolean = false,
    val scannedDevices: List<ThermometerScanResult> = emptyList(),
    val permissionGrantStatus: PermissionStatus = PermissionStatus.DECLINED
)
