package com.klewerro.mitemperature2alt.temperatureSensor

import com.klewerro.mitemperature2alt.core.util.LocalDateTimeUtils.convertEpochSecondToLocalDateTimeUtc
import com.klewerro.mitemperature2alt.domain.model.HourlyRecord
import com.klewerro.mitemperature2alt.domain.model.LastIndexTotalRecords
import com.klewerro.mitemperature2alt.domain.model.ThermometerStatus
import no.nordicsemi.android.common.core.DataByteArray
import timber.log.Timber
import java.nio.ByteBuffer
import java.nio.ByteOrder

object Converters {
    private const val TAG = "BLE ByteArrays Converters"

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

        Timber.tag(TAG)
            .d("device temperature: $temperature, humidity: $humidity, battery level: $battery")

        return ThermometerStatus(
            temperature = temperature,
            humidity = humidity,
            voltage = battery
        )
    }

    fun convertLastIndexAndTotalRecordsToPair(dataByteArray: DataByteArray): LastIndexTotalRecords {
        val value = dataByteArray.value
        val lastIndexBytes = value.take(4).toByteArray()
        val totalRecordsBytes = value.takeLast(4).toByteArray()

        val lastIndex = ByteBuffer.wrap(
            lastIndexBytes
        ).order(ByteOrder.LITTLE_ENDIAN).getInt()
        val totalRecords = ByteBuffer.wrap(
            totalRecordsBytes
        ).order(ByteOrder.LITTLE_ENDIAN).getInt()
        return LastIndexTotalRecords(lastIndex, totalRecords)
    }

    fun convertToHourlyRecord(dataByteArray: DataByteArray): HourlyRecord {
        val value = dataByteArray.value
        val indexBytes = value.take(4).toByteArray()
        val timeBytes = value.copyOfRange(4, 8)
        val maxTempBytes = value.copyOfRange(8, 10)
        val maxHumidity = value[10]
        val minTempBytes = value.copyOfRange(11, 13)
        val minHumidity = value.last()

        val index = ByteBuffer.wrap(indexBytes).order(ByteOrder.LITTLE_ENDIAN).getInt()
        val time = ByteBuffer.wrap(timeBytes).order(ByteOrder.LITTLE_ENDIAN).getInt()
        val maxTemp = ByteBuffer.wrap(maxTempBytes).order(ByteOrder.LITTLE_ENDIAN).getShort()
        val minTemp = ByteBuffer.wrap(minTempBytes).order(ByteOrder.LITTLE_ENDIAN).getShort()
        val maxTempFloat = maxTemp / 10f
        val minTempFloat = minTemp / 10f

        return HourlyRecord(
            index,
            time.convertEpochSecondToLocalDateTimeUtc(),
            temperatureMin = minTempFloat,
            temperatureMax = maxTempFloat,
            humidityMin = minHumidity.toInt(),
            humidityMax = maxHumidity.toInt()
        )
    }
}
