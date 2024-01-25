package com.klewerro.temperatureSensor.model

import com.klewerro.mitemperaturenospyware.domain.model.ThermometerScanResult
import com.klewerro.temperatureSensor.ThermometerDeviceBleClient

data class ThermometerDeviceConnection(
    val thermometerScanResult: ThermometerScanResult,
    val thermometerDeviceBleClient: ThermometerDeviceBleClient
)
