package com.klewerro.mitemperature2alt.addThermometerPresentation.search

sealed class DeviceSearchEvent {
    data class ScanForDevices(val byUser: Boolean = true) : DeviceSearchEvent()
    data class StopScanForDevices(val byUser: Boolean = true) : DeviceSearchEvent()
    data class UpdatePermissionStatus(val permissionStatus: PermissionStatus) : DeviceSearchEvent()
    data class ErrorConnectingToSavedThermometer(val thermometerName: String) : DeviceSearchEvent()
    data object ErrorDismissed : DeviceSearchEvent()
}
