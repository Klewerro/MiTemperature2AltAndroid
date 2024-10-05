package com.klewerro.mitemperature2alt.coreTest.fake

import com.klewerro.mitemperature2alt.domain.model.HourlyRecord
import com.klewerro.mitemperature2alt.domain.repository.HourlyRecordRepository

class FakeHourlyRecordRepository : HourlyRecordRepository {

    var hourlyRecords: MutableMap<String, MutableList<HourlyRecord>> = mutableMapOf()

    override suspend fun saveHourRecords(macAddress: String, records: List<HourlyRecord>) {
        hourlyRecords[macAddress]?.addAll(records)
    }
}
