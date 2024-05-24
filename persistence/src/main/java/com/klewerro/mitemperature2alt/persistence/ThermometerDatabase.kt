package com.klewerro.mitemperature2alt.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import com.klewerro.mitemperature2alt.persistence.dao.ThermometerDao
import com.klewerro.mitemperature2alt.persistence.entity.ThermometerEntity

@Database(
    entities = [ThermometerEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ThermometerDatabase : RoomDatabase() {
    abstract val thermometerDao: ThermometerDao
}
