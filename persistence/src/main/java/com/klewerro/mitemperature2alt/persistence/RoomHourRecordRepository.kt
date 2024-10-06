package com.klewerro.mitemperature2alt.persistence

import com.klewerro.mitemperature2alt.domain.model.HourlyRecord
import com.klewerro.mitemperature2alt.domain.repository.HourlyRecordRepository
import com.klewerro.mitemperature2alt.persistence.dao.HourRecordDao
import com.klewerro.mitemperature2alt.persistence.mapper.mapToHourlyRecord
import com.klewerro.mitemperature2alt.persistence.mapper.mapToHourlyRecordEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomHourRecordRepository(private val hourRecordDao: HourRecordDao) : HourlyRecordRepository {
    override suspend fun saveHourRecords(macAddress: String, records: List<HourlyRecord>) {
        hourRecordDao.insertHourRecords(
            *records.map {
                it.mapToHourlyRecordEntity(macAddress)
            }.toTypedArray()
        )
    }

    override fun observeAllThermometerRecords(address: String): Flow<List<HourlyRecord>> =
        hourRecordDao
            .observeThermometerAll(address)
            .map {
                it.map {
                    it.mapToHourlyRecord()
                }
            }
}
