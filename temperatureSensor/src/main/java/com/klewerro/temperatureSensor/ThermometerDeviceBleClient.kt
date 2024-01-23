package com.klewerro.temperatureSensor

import android.annotation.SuppressLint
import no.nordicsemi.android.kotlin.ble.client.main.callback.ClientBleGatt

@SuppressLint("MissingPermission")
class ThermometerDeviceBleClient(private val connection: ClientBleGatt) {

    suspend fun discoverDeviceOperations() {
        val services = connection.discoverServices()
    }
}
