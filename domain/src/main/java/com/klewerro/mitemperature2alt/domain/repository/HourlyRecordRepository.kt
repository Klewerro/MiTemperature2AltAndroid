package com.klewerro.mitemperature2alt.domain.repository

import com.klewerro.mitemperature2alt.domain.model.HourlyRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface HourlyRecordRepository {
    suspend fun saveHourRecords(macAddress: String, records: List<HourlyRecord>)
    fun observeAllThermometerRecords(address: String): Flow<List<HourlyRecord>>
    fun getThermometerRange(
        address: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<HourlyRecord>>
}
