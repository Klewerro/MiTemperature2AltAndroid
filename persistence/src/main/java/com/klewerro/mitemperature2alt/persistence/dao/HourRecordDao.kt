package com.klewerro.mitemperature2alt.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.klewerro.mitemperature2alt.persistence.entity.HourRecordEntity

@Dao
interface HourRecordDao {

    @Query("SELECT * FROM hour_record")
    suspend fun getAll(): List<HourRecordEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHourRecords(vararg hourRecords: HourRecordEntity)
}
