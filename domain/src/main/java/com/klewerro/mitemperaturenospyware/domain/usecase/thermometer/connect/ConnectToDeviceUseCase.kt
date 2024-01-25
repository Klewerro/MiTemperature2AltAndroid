package com.klewerro.mitemperaturenospyware.domain.usecase.thermometer.connect

import com.klewerro.mitemperaturenospyware.domain.repository.ThermometerRepository
import kotlinx.coroutines.CoroutineScope

class ConnectToDeviceUseCase(private val thermometerRepository: ThermometerRepository) {
    suspend operator fun invoke(viewModelScope: CoroutineScope, address: String) =
        thermometerRepository.connectToDevice(viewModelScope, address)
}
