package com.klewerro.mitemperaturenospyware.domain.usecase.thermometer.scan

import com.klewerro.mitemperaturenospyware.domain.model.ConnectionStatus
import com.klewerro.mitemperaturenospyware.domain.model.ThermometerScanResult
import com.klewerro.mitemperaturenospyware.domain.repository.ThermometerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class SearchedDevicesUseCase(private val thermometerRepository: ThermometerRepository) {

    operator fun invoke(): Flow<List<ThermometerScanResult>> = combine(
        thermometerRepository.scannedDevices,
        thermometerRepository.connectedDevicesStatuses,
        thermometerRepository.connectingToDeviceAddress,
        thermometerRepository.rssiStrengths
    ) { scanned, connected, currentlyConnectingDeviceAddress, rssi ->
        val connectingUiDeviceIndex =
            scanned.indexOfFirst { it.address == currentlyConnectingDeviceAddress }

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
                    connectingUiDeviceIndex > -1 && connectingUiDeviceIndex == mapIndex -> ConnectionStatus.CONNECTING
                    isConnected -> ConnectionStatus.CONNECTED
                    else -> ConnectionStatus.NOT_CONNECTED
                }
            )
        }
    }
}
