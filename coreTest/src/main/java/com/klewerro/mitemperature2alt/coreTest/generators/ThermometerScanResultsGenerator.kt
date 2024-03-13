package com.klewerro.mitemperature2alt.coreTest.generators

import com.klewerro.mitemperature2alt.domain.model.ConnectionStatus
import com.klewerro.mitemperature2alt.domain.model.ThermometerScanResult

object ThermometerScanResultsGenerator {

    var mac1 = "00:11:11:11:11:11"
    var mac2 = "00:22:22:22:22:22"

    var scanResult1 = ThermometerScanResult(
        name = "Thermometer device",
        address = mac1,
        rssi = -30,
        connectionStatus = ConnectionStatus.NOT_CONNECTED
    )

    var scanResult2 = ThermometerScanResult(
        name = "Thermometer device",
        address = mac2,
        rssi = -60,
        connectionStatus = ConnectionStatus.NOT_CONNECTED
    )

    val listOfScanResults = listOf(scanResult1, scanResult2)
}
