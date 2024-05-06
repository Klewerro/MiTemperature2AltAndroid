package com.klewerro.mitemperature2alt.domain.usecase.thermometer.scan

import com.klewerro.mitemperature2alt.domain.model.ConnectionStatus
import com.klewerro.mitemperature2alt.domain.model.ThermometerScanResult
import com.klewerro.mitemperature2alt.domain.repository.ThermometerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class SearchedDevicesUseCase(private val thermometerRepository: ThermometerRepository) {

    operator fun invoke(): Flow<List<ThermometerScanResult>> = combine(
        thermometerRepository.scannedDevices,
        thermometerRepository.connectedDevicesStatuses,
        thermometerRepository.rssiStrengths
    ) { scanned, connected, rssi ->

        scanned.mapIndexed { mapIndex, thermometerBleScanResult ->
            val isConnected = connected.any { it.key == thermometerBleScanResult.address }
            ThermometerScanResult(
                name = thermometerBleScanResult.name,
                address = thermometerBleScanResult.address,
                rssi = if (isConnected) {
                    rssi[thermometerBleScanResult.address] ?: thermometerBleScanResult.rssi
                } else {
                    thermometerBleScanResult.rssi
                },
                connectionStatus = when {
                    isConnected -> ConnectionStatus.CONNECTED
                    else -> ConnectionStatus.NOT_CONNECTED
                }
            )
        }
    }
}
