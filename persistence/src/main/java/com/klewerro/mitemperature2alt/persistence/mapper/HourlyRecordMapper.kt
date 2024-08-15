package com.klewerro.mitemperature2alt.persistence.mapper

import com.klewerro.mitemperature2alt.domain.model.HourlyRecord
import com.klewerro.mitemperature2alt.persistence.entity.HourRecordEntity

fun HourlyRecord.mapToHourlyRecordEntity(thermometerAddress: String) = HourRecordEntity(
    index = this.index,
    time = this.time,
    temperatureMin = this.temperatureMin,
    temperatureMax = this.temperatureMax,
    humidityMin = this.humidityMin,
    humidityMax = this.humidityMax,
    thermometerAddress = thermometerAddress
)

fun HourRecordEntity.mapToHourlyRecord() = HourlyRecord(
    index = this.index,
    time = this.time,
    temperatureMin = this.temperatureMin,
    temperatureMax = this.temperatureMax,
    humidityMin = this.humidityMin,
    humidityMax = this.humidityMax
)
