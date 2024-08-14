package com.klewerro.mitemperature2alt.temperatureSensor

import java.util.UUID

object BleConstants {
    const val DEVICE_NAME = "LYWSD03MMC"
    const val DEVICE_SERVICE = "ebe0ccb0-7a0a-4b0c-8a1a-6ff2997da3a6"
    const val READ_RSSI_DELAY = 10_000L

    const val CHARACTERISTIC_DEVICE_TEMPERATURE_HUMIDITY = "ebe0ccc1-7a0a-4b0c-8a1a-6ff2997da3a6"
    const val CHARACTERISTIC_LAST_INDEX_TOTAL_RECORDS = "ebe0ccb9-7a0a-4b0c-8a1a-6ff2997da3a6"
    const val CHARACTERISTIC_HOURLY_RECORDS = "ebe0ccbc-7a0a-4b0c-8a1a-6ff2997da3a6"

    fun String.toUUID(): UUID = UUID.fromString(this)
}

// Description of most of the characteristics https://github.com/JsBergbau/MiTemperature2/issues/1
