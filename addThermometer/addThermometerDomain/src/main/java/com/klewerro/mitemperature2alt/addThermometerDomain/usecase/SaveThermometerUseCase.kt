package com.klewerro.mitemperature2alt.addThermometerDomain.usecase

import com.klewerro.mitemperature2alt.domain.repository.PersistenceRepository

class SaveThermometerUseCase(private val repository: PersistenceRepository) {
    suspend operator fun invoke(macAddress: String, name: String) =
        repository.saveThermometer(name, macAddress)
}
