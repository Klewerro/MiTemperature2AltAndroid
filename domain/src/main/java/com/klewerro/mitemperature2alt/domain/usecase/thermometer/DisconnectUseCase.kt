package com.klewerro.mitemperature2alt.domain.usecase.thermometer

import com.klewerro.mitemperature2alt.domain.repository.ThermometerRepository

class DisconnectUseCase(private val thermometerRepository: ThermometerRepository) {
    suspend operator fun invoke(deviceAddress: String) =
        thermometerRepository.disconnect(deviceAddress)
}
