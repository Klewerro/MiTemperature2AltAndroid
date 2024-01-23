package com.klewerro.temperatureSensor.model

data class ThermometerBleDevice(
    val name: String,
    val address: String,
    val rssi: Int
) {
    override fun equals(other: Any?): Boolean {
        return other is ThermometerBleDevice && other.address == this.address
    }

    override fun hashCode(): Int {
        return address.hashCode()
    }
}
