package com.klewerro.mitemperature2alt.temperatureSensor

import android.util.Log
import com.klewerro.mitemperature2alt.domain.model.ThermometerStatus
import no.nordicsemi.android.common.core.DataByteArray
import java.nio.ByteBuffer
import java.nio.ByteOrder

object Converters {
    const val TAG = "BLE ByteArrays Converters"

    fun convertTemperatureHumidityVoltageToCurrentThermometerStatus(
        dataByteArray: DataByteArray
    ): ThermometerStatus {
        val value = dataByteArray.value
        val temperatureBytes = value.take(2).toByteArray()
        val humidityByte = value[2]
        val batteryBytes = value.takeLast(2).toByteArray()

        val temperatureShort = ByteBuffer.wrap(
            temperatureBytes
        ).order(ByteOrder.LITTLE_ENDIAN).getShort()
        val humidity = humidityByte.toInt()
        val batteryShort = ByteBuffer.wrap(batteryBytes).order(ByteOrder.LITTLE_ENDIAN).getShort()

        val temperature = temperatureShort / 100f
        val battery = batteryShort / 1000f

        Log.d(
            TAG,
            "device temperature: $temperature, humidity: $humidity, battery level: $battery"
        )

        return ThermometerStatus(
            temperature = temperature,
            humidity = humidity,
            voltage = battery
        )
    }
}
