package com.klewerro.mitemperaturenospyware.coreTest.fake

import com.klewerro.mitemperaturenospyware.coreTest.generators.ThermometerScanResultsGenerator
import com.klewerro.mitemperaturenospyware.coreTest.generators.ThermometerStatusGenerator
import com.klewerro.mitemperaturenospyware.domain.model.ThermometerScanResult
import com.klewerro.mitemperaturenospyware.temperatureSensor.ThermometerDeviceBleClient
import com.klewerro.mitemperaturenospyware.temperatureSensor.contracts.ThermometerDevicesBleScanner
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FakeThermometerDevicesBleScanner : ThermometerDevicesBleScanner {
    var isScanningInternal = MutableStateFlow(false)
    override val isScanning: StateFlow<Boolean> = isScanningInternal

    private val _bleDevices = MutableStateFlow<List<ThermometerScanResult>>(emptyList())
    override val bleDevices: StateFlow<List<ThermometerScanResult>> = _bleDevices

    var isScanCancelledFlag = false
    var scanResultDelay = 1_000L
    var readThermometerStatus = ThermometerStatusGenerator.thermometerStatus1
    var mac1 = ThermometerScanResultsGenerator.mac1
    var mac2 = ThermometerScanResultsGenerator.mac2
    private var scanResult1 = ThermometerScanResultsGenerator.scanResult1
    private var scanResult2 = ThermometerScanResultsGenerator.scanResult2

    override fun scanForDevices(coroutineScope: CoroutineScope): Job {
        return coroutineScope.launch {
            isScanningInternal.update { true }
            while (!isScanCancelledFlag) {
                delay(scanResultDelay)
                updateScanResults(scanResult1)
                delay(scanResultDelay)
                updateScanResults(scanResult2)
                delay(scanResultDelay)
            }
        }.apply {
            invokeOnCompletion {
                isScanningInternal.update { false }
            }
        }
    }

    override suspend fun connectToDevice(
        coroutineScope: CoroutineScope,
        bleDeviceAddress: String
    ): ThermometerDeviceBleClient {
        delay(scanResultDelay)
        val thermometerDeviceBleClientMock = mockk<ThermometerDeviceBleClient>()
        coEvery {
            thermometerDeviceBleClientMock.readThermometerStatus()
        } returns ThermometerStatusGenerator.thermometerStatus1
        return thermometerDeviceBleClientMock
    }

    private fun updateScanResults(scanResult: ThermometerScanResult) {
        val test = (-99..-1).filter { it != scanResult.rssi }.random()
        val newResult = scanResult.copy(
            rssi = test
        )
        _bleDevices.update {
            val scanResultListIndex = it.indexOfFirst { it.address == scanResult.address }
            it.toMutableList().apply {
                if (scanResultListIndex == -1) {
                    this.add(newResult)
                } else {
                    this[scanResultListIndex] = newResult
                }
            }.toList()
        }
    }
}
