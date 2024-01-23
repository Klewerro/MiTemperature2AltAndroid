package com.klewerro.temperatureSensor.model

import no.nordicsemi.android.kotlin.ble.client.main.callback.ClientBleGatt

data class ThermometerDeviceConnection(
    val thermometerBleDevice: ThermometerBleDevice,
    val gattClient: ClientBleGatt
)
