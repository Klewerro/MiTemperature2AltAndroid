package com.klewerro.mitemperature2alt.domain.usecase.thermometer.operations

import com.klewerro.mitemperature2alt.domain.repository.ThermometerRepository

class ReadCurrentThermometerStatusUseCase(private val thermometerRepository: ThermometerRepository) {
    suspend operator fun invoke(deviceAddress: String) =
        thermometerRepository.readCurrentThermometerStatus(deviceAddress)
}
