package com.klewerro.mitemperature2alt.presentation.addHeater

import com.klewerro.mitemperature2alt.presentation.model.PermissionStatus

sealed class DeviceSearchEvent {
    data object ScanForDevices : DeviceSearchEvent()
    data object StopScanForDevices : DeviceSearchEvent()
    data class UpdatePermissionStatus(val permissionStatus: PermissionStatus) : DeviceSearchEvent()
}
