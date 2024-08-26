package com.klewerro.mitemperature2alt.presentation.mainscreen

import com.klewerro.mitemperature2alt.domain.model.Thermometer

sealed class ThermometerOperationType {
    data object Idle : ThermometerOperationType()
    data class ConnectingToDevice(val thermometerName: String) : ThermometerOperationType()
    data class RetrievingHourlyRecords(
        val thermometer: Thermometer,
        val currentRecordNumber: Int,
        val numberOrRecords: Int
    ) : ThermometerOperationType()
}
