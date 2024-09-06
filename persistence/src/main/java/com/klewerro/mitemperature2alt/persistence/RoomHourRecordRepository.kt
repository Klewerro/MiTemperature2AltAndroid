package com.klewerro.mitemperature2alt.persistence

import com.klewerro.mitemperature2alt.domain.model.HourlyRecord
import com.klewerro.mitemperature2alt.domain.repository.HourlyRecordRepository
import com.klewerro.mitemperature2alt.persistence.dao.HourRecordDao
import com.klewerro.mitemperature2alt.persistence.mapper.mapToHourlyRecordEntity

class RoomHourRecordRepository(private val hourRecordDao: HourRecordDao) : HourlyRecordRepository {
    override suspend fun saveHourRecords(macAddress: String, records: List<HourlyRecord>) {
        hourRecordDao.insertHourRecords(
            *records.map {
                it.mapToHourlyRecordEntity(macAddress)
            }.toTypedArray()
        )
    }
}
