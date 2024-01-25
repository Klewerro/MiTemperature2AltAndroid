package com.klewerro.mitemperaturenospyware.domain.usecase.thermometer.scan

import com.klewerro.mitemperaturenospyware.domain.repository.ThermometerRepository
import kotlinx.coroutines.CoroutineScope

class ScanForDevicesUseCase(private val thermometerRepository: ThermometerRepository) {
    operator fun invoke(coroutineScope: CoroutineScope) = thermometerRepository.scanForDevices(coroutineScope)
}
