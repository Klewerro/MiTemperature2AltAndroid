package com.klewerro.mitemperature2alt.presentation.mainscreen

sealed class ThermometerOperationType {
    data object Idle : ThermometerOperationType()
    data class ConnectingToDevice(val thermometerName: String) : ThermometerOperationType()
    data class RetrievingHourlyRecords(
        val thermometerName: String,
        val currentRecordNumber: Int,
        val numberOrRecords: Int
    ) : ThermometerOperationType()
}
