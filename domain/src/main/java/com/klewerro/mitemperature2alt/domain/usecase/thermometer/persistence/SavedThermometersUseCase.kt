package com.klewerro.mitemperature2alt.domain.usecase.thermometer.persistence

import com.klewerro.mitemperature2alt.domain.repository.PersistenceRepository

class SavedThermometersUseCase(private val persistenceRepository: PersistenceRepository) {
    operator fun invoke() = persistenceRepository.savedThermometers
}
