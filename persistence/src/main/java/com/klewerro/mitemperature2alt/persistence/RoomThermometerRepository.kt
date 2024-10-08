package com.klewerro.mitemperature2alt.persistence

import com.klewerro.mitemperature2alt.domain.repository.PersistenceRepository
import com.klewerro.mitemperature2alt.persistence.dao.ThermometerDao
import com.klewerro.mitemperature2alt.persistence.entity.ThermometerEntity
import com.klewerro.mitemperature2alt.persistence.mapper.toSavedThermometer
import kotlinx.coroutines.flow.map

class RoomThermometerRepository(
    private val thermometerDao: ThermometerDao
) : PersistenceRepository {

    override val savedThermometers = thermometerDao.getAllThermometers().map {
        it.map { thermometerEntity ->
            thermometerEntity.toSavedThermometer()
        }
    }

    override suspend fun saveThermometer(name: String, macAddress: String) {
        thermometerDao.insertThermometer(
            ThermometerEntity(
                address = macAddress,
                name = name
            )
        )
    }
}
