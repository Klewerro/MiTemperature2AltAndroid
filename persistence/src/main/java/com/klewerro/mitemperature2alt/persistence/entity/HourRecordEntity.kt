package com.klewerro.mitemperature2alt.persistence.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "hour_record",
    foreignKeys = [
        ForeignKey(
            entity = ThermometerEntity::class,
            parentColumns = ["address"],
            childColumns = ["thermometerAddress"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class HourRecordEntity(
    @PrimaryKey val index: Int,
    @ColumnInfo(name = "time") val epochSecondTime: Int,
    val temperatureMin: Float,
    val temperatureMax: Float,
    val humidityMin: Int,
    val humidityMax: Int,
    val thermometerAddress: String
)
