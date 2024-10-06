package com.klewerro.mitemperature2alt.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.klewerro.mitemperature2alt.persistence.entity.HourRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HourRecordDao {

    @Query("SELECT * FROM hour_record")
    suspend fun getAll(): List<HourRecordEntity>

    @Query("SELECT * FROM hour_record WHERE thermometerAddress = :address")
    fun observeThermometerAll(address: String): Flow<List<HourRecordEntity>>

    @Query(
        "SELECT * FROM hour_record " +
            "WHERE thermometerAddress = :address and time between :startTime and :endTime"
    )
    fun getThermometerRange(
        address: String,
        startTime: Int,
        endTime: Int
    ): Flow<List<HourRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHourRecords(vararg hourRecords: HourRecordEntity)
}
