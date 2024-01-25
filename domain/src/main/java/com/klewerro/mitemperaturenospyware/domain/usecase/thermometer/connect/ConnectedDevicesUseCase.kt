package com.klewerro.mitemperaturenospyware.domain.usecase.thermometer.connect

import com.klewerro.mitemperaturenospyware.domain.repository.ThermometerRepository

class ConnectedDevicesUseCase(private val thermometerRepository: ThermometerRepository) {
    operator fun invoke() = thermometerRepository.connectedDevices
}
