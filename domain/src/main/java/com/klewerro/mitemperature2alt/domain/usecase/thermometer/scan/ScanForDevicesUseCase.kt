package com.klewerro.mitemperature2alt.domain.usecase.thermometer.scan

import com.klewerro.mitemperature2alt.domain.repository.ThermometerRepository
import kotlinx.coroutines.CoroutineScope

class ScanForDevicesUseCase(private val thermometerRepository: ThermometerRepository) {
    operator fun invoke(coroutineScope: CoroutineScope) =
        thermometerRepository.scanForDevices(coroutineScope)
}
