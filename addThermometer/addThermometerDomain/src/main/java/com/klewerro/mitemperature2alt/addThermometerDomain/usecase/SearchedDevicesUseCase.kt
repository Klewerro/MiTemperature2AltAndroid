package com.klewerro.mitemperature2alt.addThermometerDomain.usecase

import com.klewerro.mitemperature2alt.domain.model.ScannedDeviceStatus
import com.klewerro.mitemperature2alt.domain.model.ThermometerScanResult
import com.klewerro.mitemperature2alt.domain.repository.PersistenceRepository
import com.klewerro.mitemperature2alt.domain.repository.ThermometerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class SearchedDevicesUseCase(
    private val thermometerRepository: ThermometerRepository,
    private val persistenceRepository: PersistenceRepository
) {
    operator fun invoke(): Flow<List<ThermometerScanResult>> = combine(
        thermometerRepository.scannedDevices,
        thermometerRepository.connectedDevicesStatuses,
        thermometerRepository.rssiStrengths,
        persistenceRepository.savedThermometers
    ) { scanned, connected, rssi, savedThermometers ->

        scanned.mapIndexed { mapIndex, thermometerBleScanResult ->
            val isConnected = connected.any { it.key == thermometerBleScanResult.address }

            val savedThermometer = savedThermometers.firstOrNull {
                it.address == thermometerBleScanResult.address
            }

            ThermometerScanResult(
                name = savedThermometer?.name ?: thermometerBleScanResult.name,
                address = thermometerBleScanResult.address,
                rssi = if (isConnected) {
                    rssi[thermometerBleScanResult.address] ?: thermometerBleScanResult.rssi
                } else {
                    thermometerBleScanResult.rssi
                },
                scannedDeviceStatus = when {
                    savedThermometer != null -> {
                        ScannedDeviceStatus.SAVED
                    }
                    isConnected -> ScannedDeviceStatus.CONNECTED
                    else -> ScannedDeviceStatus.NOT_CONNECTED
                }
            )
        }
    }
}
