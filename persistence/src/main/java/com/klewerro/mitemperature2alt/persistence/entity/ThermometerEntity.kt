package com.klewerro.mitemperature2alt.persistence.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "thermometer")
data class ThermometerEntity(
    @PrimaryKey val address: String,
    val name: String
)
