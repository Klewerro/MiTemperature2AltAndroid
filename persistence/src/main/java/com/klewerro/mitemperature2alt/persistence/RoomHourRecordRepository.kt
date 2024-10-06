package com.klewerro.mitemperature2alt.persistence

import com.klewerro.mitemperature2alt.core.util.LocalDateTimeUtils.toEpochSecondUtc
import com.klewerro.mitemperature2alt.domain.model.HourlyRecord
import com.klewerro.mitemperature2alt.domain.repository.HourlyRecordRepository
import com.klewerro.mitemperature2alt.persistence.dao.HourRecordDao
import com.klewerro.mitemperature2alt.persistence.mapper.mapToHourlyRecord
import com.klewerro.mitemperature2alt.persistence.mapper.mapToHourlyRecordEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.atTime

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

    override fun getThermometerRange(
        address: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<HourlyRecord>> {
        val start = startDate.atTime(0, 0).toEpochSecondUtc()
        val end = endDate.atTime(23, 59).toEpochSecondUtc()
        return hourRecordDao
            .getThermometerRange(address, start, end)
            .map {
                it.map {
                    it.mapToHourlyRecord()
                }
            }
    }
}
