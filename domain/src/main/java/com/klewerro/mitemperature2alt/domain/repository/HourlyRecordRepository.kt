package com.klewerro.mitemperature2alt.domain.repository

import com.klewerro.mitemperature2alt.domain.model.HourlyRecord

interface HourlyRecordRepository {
    suspend fun saveHourRecords(macAddress: String, records: List<HourlyRecord>)
}
