package com.klewerro.mitemperature2alt.domain.usecase

import com.klewerro.mitemperature2alt.domain.model.Progress
import com.klewerro.mitemperature2alt.domain.repository.ThermometerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.flow.update

class GetHourlyResultsUseCase(private val thermometerRepository: ThermometerRepository) {

    private var _progress = MutableStateFlow(Progress(0, 0))
    val progress = _progress
        .dropWhile { it.currentItemNumber == 0 && it.itemsCount == 0 }

    suspend operator fun invoke(coroutineScope: CoroutineScope, deviceAddress: String) {
        val hourlyRecords = thermometerRepository.readThermometerHourlyRecords(
            coroutineScope,
            deviceAddress,
            0
        ) { currentItemNumber, totalRecords ->
            _progress.update { Progress(currentItemNumber, totalRecords) }
        }
        _progress.update {
            Progress(0, 0)
        }
        hourlyRecords.toString()
        // Todo Save into database after commit
    }
}
