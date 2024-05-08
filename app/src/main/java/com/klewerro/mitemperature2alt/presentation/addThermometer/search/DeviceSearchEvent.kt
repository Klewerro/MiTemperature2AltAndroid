package com.klewerro.mitemperature2alt.presentation.addThermometer.search

import com.klewerro.mitemperature2alt.presentation.model.PermissionStatus

sealed class DeviceSearchEvent {
    data class ScanForDevices(val byUser: Boolean = true) : DeviceSearchEvent()
    data class StopScanForDevices(val byUser: Boolean = true) : DeviceSearchEvent()
    data class UpdatePermissionStatus(val permissionStatus: PermissionStatus) : DeviceSearchEvent()
}
