package com.klewerro.mitemperature2alt.addThermometerDomain.usecase

import com.klewerro.mitemperature2alt.domain.repository.ThermometerRepository
import kotlinx.coroutines.CoroutineScope

class ConnectToDeviceUseCase(
    private val thermometerRepository: ThermometerRepository
) {
    suspend operator fun invoke(viewModelScope: CoroutineScope, address: String) {
        thermometerRepository.connectToDevice(viewModelScope, address)
        thermometerRepository.subscribeToCurrentThermometerStatus(address, viewModelScope)
        thermometerRepository.subscribeToRssi(address, viewModelScope)
        thermometerRepository.subscribeToConnectionStatus(address, viewModelScope)
    }
}
