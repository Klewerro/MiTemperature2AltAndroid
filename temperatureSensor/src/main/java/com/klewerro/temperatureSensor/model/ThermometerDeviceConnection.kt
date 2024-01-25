package com.klewerro.temperatureSensor.model

import com.klewerro.temperatureSensor.ThermometerDeviceBleClient

data class ThermometerDeviceConnection(
    val thermometerScanResult: ThermometerScanResult,
    val thermometerDeviceBleClient: ThermometerDeviceBleClient
)
