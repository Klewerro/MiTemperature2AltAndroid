package com.klewerro.mitemperaturenospyware.domain.usecase.thermometer.scan

import com.klewerro.mitemperaturenospyware.domain.model.ConnectionStatus
import com.klewerro.mitemperaturenospyware.domain.model.ThermometerDevice
import com.klewerro.mitemperaturenospyware.domain.repository.ThermometerRepository
import kotlinx.coroutines.flow.combine

class SearchedDevicesUseCase(private val thermometerRepository: ThermometerRepository) {
    private val scannedDevices = thermometerRepository.scannedDevices
    private val connectedDevices = thermometerRepository.connectedDevices

    operator fun invoke() = combine(
        scannedDevices,
        connectedDevices,
        thermometerRepository.connectingToDeviceAddress
    ) { scanned, connected, currentlyConnectingDeviceAddress ->
        val connectingUiDeviceIndex =
            scanned.indexOfFirst { it.address == currentlyConnectingDeviceAddress }

        scanned.mapIndexed { mapIndex, thermometerBleScanResult ->
            val isConnected = connected.any { it.address == thermometerBleScanResult.address }
            ThermometerDevice(
                name = thermometerBleScanResult.name,
                address = thermometerBleScanResult.address,
                rssi = thermometerBleScanResult.rssi,
                connectionStatus = when {
                    connectingUiDeviceIndex > -1 && connectingUiDeviceIndex == mapIndex -> ConnectionStatus.CONNECTING
                    isConnected -> ConnectionStatus.CONNECTED
                    else -> ConnectionStatus.NOT_CONNECTED
                },
                status = null
            )
        }
    }
}
