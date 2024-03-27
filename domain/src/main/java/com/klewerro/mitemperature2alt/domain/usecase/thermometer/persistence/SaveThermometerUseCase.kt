package com.klewerro.mitemperature2alt.domain.usecase.thermometer.persistence

import com.klewerro.mitemperature2alt.domain.repository.PersistenceRepository

class SaveThermometerUseCase(private val repository: PersistenceRepository) {
    suspend operator fun invoke(macAddress: String, name: String) = // Todo: Name validation
        repository.saveThermometer(
            macAddress,
            name
        )
}
