package com.klewerro.mitemperature2alt.coreTest.fake

import com.klewerro.mitemperature2alt.domain.model.SavedThermometer
import com.klewerro.mitemperature2alt.domain.repository.PersistenceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FakePersistenceRepository : PersistenceRepository {

    private var _savedThermometers = MutableStateFlow<List<SavedThermometer>>(emptyList())

    override val savedThermometers: Flow<List<SavedThermometer>> = _savedThermometers

    override suspend fun saveThermometer(name: String, macAddress: String) {
        _savedThermometers.update {
            val savedThermometer = SavedThermometer(macAddress, name)
            it.plus(savedThermometer)
        }
    }
}
