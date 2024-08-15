package com.klewerro.mitemperature2alt.domain.usecase

import com.klewerro.mitemperature2alt.domain.model.Progress
import com.klewerro.mitemperature2alt.domain.repository.HourlyRecordRepository
import com.klewerro.mitemperature2alt.domain.repository.ThermometerRepository
import kotlinx.coroutines.CoroutineScope

class GetHourlyResultsUseCase(
    private val thermometerRepository: ThermometerRepository,
    private val hourlyRecordRepository: HourlyRecordRepository
) {

    suspend operator fun invoke(
        coroutineScope: CoroutineScope,
        deviceAddress: String,
        progressCallback: (Progress) -> Unit
    ) {
        val hourlyRecords = thermometerRepository.readThermometerHourlyRecords(
            coroutineScope,
            deviceAddress,
            0
        ) { currentItemNumber, totalRecords ->
            progressCallback(Progress(currentItemNumber, totalRecords))
        }
        hourlyRecords?.let {
            hourlyRecordRepository.saveHourRecords(deviceAddress, it)
        }
    }
}
