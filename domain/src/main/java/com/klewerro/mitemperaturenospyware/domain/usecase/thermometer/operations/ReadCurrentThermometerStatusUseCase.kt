package com.klewerro.mitemperaturenospyware.domain.usecase.thermometer.operations

import com.klewerro.mitemperaturenospyware.domain.repository.ThermometerRepository

class ReadCurrentThermometerStatusUseCase(private val thermometerRepository: ThermometerRepository) {
    suspend operator fun invoke(deviceAddress: String) =
        thermometerRepository.readCurrentThermometerStatus(deviceAddress)
}
