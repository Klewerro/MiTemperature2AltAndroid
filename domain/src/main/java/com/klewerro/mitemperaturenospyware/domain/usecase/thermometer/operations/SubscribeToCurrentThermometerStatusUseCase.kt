package com.klewerro.mitemperaturenospyware.domain.usecase.thermometer.operations

import com.klewerro.mitemperaturenospyware.domain.repository.ThermometerRepository
import kotlinx.coroutines.CoroutineScope

class SubscribeToCurrentThermometerStatusUseCase(private val thermometerRepository: ThermometerRepository) {
    suspend operator fun invoke(deviceAddress: String, viewModelScope: CoroutineScope) {
        thermometerRepository.subscribeToCurrentThermometerStatus(deviceAddress, viewModelScope)
        thermometerRepository.subscribeToRssi(deviceAddress, viewModelScope)
    }
}
