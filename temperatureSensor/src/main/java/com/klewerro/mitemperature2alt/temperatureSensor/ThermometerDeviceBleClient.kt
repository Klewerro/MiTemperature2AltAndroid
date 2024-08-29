package com.klewerro.mitemperature2alt.temperatureSensor

import android.annotation.SuppressLint
import com.klewerro.mitemperature2alt.domain.model.HourlyRecord
import com.klewerro.mitemperature2alt.domain.model.LastIndexTotalRecords
import com.klewerro.mitemperature2alt.domain.model.ThermometerStatus
import com.klewerro.mitemperature2alt.temperatureSensor.BleConstants.toUUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import no.nordicsemi.android.kotlin.ble.client.main.callback.ClientBleGatt
import no.nordicsemi.android.kotlin.ble.client.main.service.ClientBleGattCharacteristic
import no.nordicsemi.android.kotlin.ble.core.data.GattConnectionState

@SuppressLint("MissingPermission")
class ThermometerDeviceBleClient(private val connection: ClientBleGatt) {

    private var temperatureHumidityCharacteristic: ClientBleGattCharacteristic? = null
    private var lastIndexTotalRecordsCharacteristic: ClientBleGattCharacteristic? = null
    private var hourlyRecordsCharacteristic: ClientBleGattCharacteristic? = null

    val connectionState: Flow<GattConnectionState> = connection.connectionState

    suspend fun discoverDeviceOperations() {
        val services = connection.discoverServices()
        val service = services.findService(BleConstants.DEVICE_SERVICE.toUUID())
        temperatureHumidityCharacteristic = service?.findCharacteristic(
            BleConstants.CHARACTERISTIC_DEVICE_TEMPERATURE_HUMIDITY.toUUID()
        )
        lastIndexTotalRecordsCharacteristic = service?.findCharacteristic(
            BleConstants.CHARACTERISTIC_LAST_INDEX_TOTAL_RECORDS.toUUID()
        )
        hourlyRecordsCharacteristic = service?.findCharacteristic(
            BleConstants.CHARACTERISTIC_HOURLY_RECORDS.toUUID()
        )
    }

    fun disconnect() {
        connection.disconnect()
    }

    suspend fun readThermometerStatus(): ThermometerStatus? {
        val readResult = temperatureHumidityCharacteristic?.read()
        return readResult?.let {
            Converters.convertTemperatureHumidityVoltageToCurrentThermometerStatus(it)
        }
    }

    suspend fun readLastIndexAndTotalRecords(): LastIndexTotalRecords? {
        val readResult = lastIndexTotalRecordsCharacteristic?.read()
        return readResult?.let {
            Converters.convertLastIndexAndTotalRecordsToPair(it)
        }
    }

    suspend fun subscribeToThermometerHourlyRecords(): Flow<HourlyRecord>? =
        hourlyRecordsCharacteristic?.getNotifications()?.map {
            Converters.convertToHourlyRecord(it)
        }

    suspend fun subscribeToThermometerStatus(): Flow<ThermometerStatus>? =
        temperatureHumidityCharacteristic?.getNotifications()?.map {
            Converters.convertTemperatureHumidityVoltageToCurrentThermometerStatus(it)
        }

    suspend fun readRssi(): Int = connection.readRssi()
}
