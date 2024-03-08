package com.klewerro.mitemperaturenospyware.domain.usecase.thermometer.connect

import com.klewerro.mitemperaturenospyware.domain.model.ThermometerDevice
import com.klewerro.mitemperaturenospyware.domain.repository.ThermometerRepository
import kotlinx.coroutines.flow.combine

class ConnectedDevicesUseCase(private val thermometerRepository: ThermometerRepository) {
    operator fun invoke() = thermometerRepository.connectedDevicesStatuses
        .combine(thermometerRepository.rssiStrengths) { status, rssi ->
            status.map { entry ->
                ThermometerDevice(
                    name = entry.key,
                    address = entry.key,
                    temperature = entry.value.temperature,
                    humidity = entry.value.humidity,
                    voltage = entry.value.voltage,
                    rssi = rssi[entry.key]
                )
            }
        }
}
