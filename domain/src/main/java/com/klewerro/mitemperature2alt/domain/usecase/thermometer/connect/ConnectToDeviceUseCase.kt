package com.klewerro.mitemperature2alt.domain.usecase.thermometer.connect

import com.klewerro.mitemperature2alt.domain.repository.ThermometerRepository
import kotlinx.coroutines.CoroutineScope

class ConnectToDeviceUseCase(
    private val thermometerRepository: ThermometerRepository
) {
    suspend operator fun invoke(viewModelScope: CoroutineScope, address: String) =
        thermometerRepository.connectToDevice(viewModelScope, address)
}
