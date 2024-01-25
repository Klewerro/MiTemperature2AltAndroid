package com.klewerro.mitemperaturenospyware.domain.usecase.thermometer.scan

import com.klewerro.mitemperaturenospyware.domain.repository.ThermometerRepository

class IsScanningForDevicesUseCase(private val thermometerRepository: ThermometerRepository) {
    operator fun invoke() = thermometerRepository.isScanningForDevices
}
