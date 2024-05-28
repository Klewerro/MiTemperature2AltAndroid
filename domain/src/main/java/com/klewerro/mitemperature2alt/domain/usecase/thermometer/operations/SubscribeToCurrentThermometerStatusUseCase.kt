package com.klewerro.mitemperature2alt.domain.usecase.thermometer.operations

import com.klewerro.mitemperature2alt.domain.repository.ThermometerRepository
import kotlinx.coroutines.CoroutineScope

class SubscribeToCurrentThermometerStatusUseCase(
    private val thermometerRepository: ThermometerRepository
) {
    suspend operator fun invoke(deviceAddress: String, viewModelScope: CoroutineScope) {
        thermometerRepository.subscribeToCurrentThermometerStatus(deviceAddress, viewModelScope)
        thermometerRepository.subscribeToRssi(deviceAddress, viewModelScope)
        thermometerRepository.subscribeToConnectionStatus(deviceAddress, viewModelScope)
    }
}
