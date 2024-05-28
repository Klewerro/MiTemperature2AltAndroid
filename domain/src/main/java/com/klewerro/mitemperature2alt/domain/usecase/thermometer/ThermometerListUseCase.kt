package com.klewerro.mitemperature2alt.domain.usecase.thermometer

import com.klewerro.mitemperature2alt.domain.mapper.RssiStrengthMapper
import com.klewerro.mitemperature2alt.domain.model.Thermometer
import com.klewerro.mitemperature2alt.domain.model.ThermometerConnectionStatus
import com.klewerro.mitemperature2alt.domain.repository.PersistenceRepository
import com.klewerro.mitemperature2alt.domain.repository.ThermometerRepository
import kotlinx.coroutines.flow.combine

/**
 * UseCase contains list of merged saved and connected thermometers
 */
class ThermometerListUseCase(
    private val persistenceRepository: PersistenceRepository,
    private val thermometerRepository: ThermometerRepository
) {
    operator fun invoke() = combine(
        persistenceRepository.savedThermometers,
        thermometerRepository.connectedDevicesStatuses,
        thermometerRepository.thermometerConnectionStatuses,
        thermometerRepository.rssiStrengths
    ) { savedThermometers, connectedThermometers, thermometerConnectionStatuses, rssiMap ->
        savedThermometers.map { savedThermometer ->
            val address = savedThermometer.address
            val thermometerStatus = connectedThermometers[address]
            val thermometerConnectionStatus = thermometerConnectionStatuses[address]
            val rssi = rssiMap[address]

            Thermometer(
                name = savedThermometer.name,
                address = savedThermometer.address,
                temperature = thermometerStatus?.temperature ?: 5.0f,
                humidity = thermometerStatus?.humidity ?: 0,
                voltage = thermometerStatus?.voltage ?: 0.0f,
                rssi = RssiStrengthMapper.map(rssi),
                thermometerConnectionStatus = thermometerConnectionStatus
                    ?: ThermometerConnectionStatus.DISCONNECTED
            )
        }
    }
}
