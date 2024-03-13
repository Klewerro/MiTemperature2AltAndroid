package com.klewerro.mitemperature2alt.domain.usecase.thermometer.scan

import com.klewerro.mitemperature2alt.domain.repository.ThermometerRepository

class IsScanningForDevicesUseCase(private val thermometerRepository: ThermometerRepository) {
    operator fun invoke() = thermometerRepository.isScanningForDevices
}
