package com.klewerro.mitemperature2alt.domain.repository

import com.klewerro.mitemperature2alt.domain.model.SavedThermometer
import kotlinx.coroutines.flow.Flow

interface PersistenceRepository {
    val savedThermometers: Flow<List<SavedThermometer>>
    suspend fun saveThermometer(name: String, macAddress: String)
}
