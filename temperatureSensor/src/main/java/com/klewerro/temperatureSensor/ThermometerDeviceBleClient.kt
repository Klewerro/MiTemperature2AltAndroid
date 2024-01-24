package com.klewerro.temperatureSensor

import android.annotation.SuppressLint
import android.util.Log
import com.klewerro.temperatureSensor.BleConstants.toUUID
import com.klewerro.temperatureSensor.model.CurrentThermometerStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import no.nordicsemi.android.common.core.DataByteArray
import no.nordicsemi.android.kotlin.ble.client.main.callback.ClientBleGatt
import no.nordicsemi.android.kotlin.ble.client.main.service.ClientBleGattCharacteristic
import java.nio.ByteBuffer
import java.nio.ByteOrder

@SuppressLint("MissingPermission")
class ThermometerDeviceBleClient(private val connection: ClientBleGatt) {

    private var temperatureHumidityCharacteristic: ClientBleGattCharacteristic? = null

    suspend fun discoverDeviceOperations() {
        val services = connection.discoverServices()
        val service = services.findService(BleConstants.DEVICE_SERVICE.toUUID())
        temperatureHumidityCharacteristic = service?.findCharacteristic(BleConstants.CHARACTERISTIC_DEVICE_TEMPERATURE_HUMIDITY.toUUID())
    }

    suspend fun readThermometerStatus(): CurrentThermometerStatus? {
        val readResult = temperatureHumidityCharacteristic?.read()
        return readResult?.let {
            convertTemperatureHumidityVoltageToCurrentThermometerStatus(it)
        }
    }

    suspend fun subscribeToThermometerStatus(): Flow<CurrentThermometerStatus>? {
        return temperatureHumidityCharacteristic?.getNotifications()?.map {
            convertTemperatureHumidityVoltageToCurrentThermometerStatus(it)
        }
    }

    private fun convertTemperatureHumidityVoltageToCurrentThermometerStatus(dataByteArray: DataByteArray): CurrentThermometerStatus {
        val value = dataByteArray.value
        val temperatureBytes = value.take(2).toByteArray()
        val humidityByte = value[2]
        val batteryBytes = value.takeLast(2).toByteArray()

        val temperatureShort = ByteBuffer.wrap(temperatureBytes).order(ByteOrder.LITTLE_ENDIAN).getShort()
        val humidity = humidityByte.toInt()
        val batteryShort = ByteBuffer.wrap(batteryBytes).order(ByteOrder.LITTLE_ENDIAN).getShort()

        val temperature = temperatureShort / 100f
        val battery = batteryShort / 1000f

        Log.d(
            TAG,
            "device temperature: $temperature, humidity: $humidity, battery level: $battery"
        )

        return CurrentThermometerStatus(
            temperature = temperature,
            humidity = humidity,
            voltage = battery
        )
    }

    private companion object {
        const val TAG = "ThermometerDeviceBleClient"
    }
}
