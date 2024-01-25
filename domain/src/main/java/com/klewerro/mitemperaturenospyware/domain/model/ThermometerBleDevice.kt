package com.klewerro.mitemperaturenospyware.domain.model

data class ThermometerBleDevice(
    val address: String,
    val name: String,
    val rssi: Int,
    val status: CurrentThermometerStatus?

) {
    companion object {
        fun fromScanResult(thermometerScanResult: ThermometerScanResult) = ThermometerBleDevice(
            address = thermometerScanResult.address,
            name = thermometerScanResult.name,
            rssi = thermometerScanResult.rssi,
            null
        )
    }
}
