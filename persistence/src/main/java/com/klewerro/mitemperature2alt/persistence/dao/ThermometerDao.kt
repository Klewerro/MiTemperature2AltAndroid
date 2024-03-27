package com.klewerro.mitemperature2alt.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.klewerro.mitemperature2alt.persistence.entity.ThermometerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ThermometerDao {

    @Query("SELECT * from thermometer")
    fun getAllThermometers(): Flow<List<ThermometerEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertThermometer(thermometer: ThermometerEntity): Long
}
