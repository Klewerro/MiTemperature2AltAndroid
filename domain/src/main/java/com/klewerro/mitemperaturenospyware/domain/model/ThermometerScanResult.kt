package com.klewerro.mitemperaturenospyware.domain.model

data class ThermometerScanResult(
    val name: String,
    val address: String,
    val rssi: Int
) {
    override fun equals(other: Any?): Boolean {
        return other is ThermometerScanResult && other.address == this.address
    }

    override fun hashCode(): Int {
        return address.hashCode()
    }
}
