package com.klewerro.mitemperature2alt.domain.usecase

import com.klewerro.mitemperature2alt.domain.repository.ThermometerRepository
import kotlinx.coroutines.CoroutineScope

class ScanAndConnectToDeviceUseCase(private val thermometerRepository: ThermometerRepository) {
    suspend operator fun invoke(viewModelScope: CoroutineScope, address: String): Result<Unit> =
        try {
            thermometerRepository.scanAndConnect(viewModelScope, address)
            Result.success(Unit)
        } catch (illegalStateException: Exception) {
            Result.failure(illegalStateException)
        }
}
