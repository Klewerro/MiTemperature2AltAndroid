package com.klewerro.mitemperature2alt.coreTest.fake

import com.klewerro.mitemperature2alt.coreTest.generators.ThermometerScanResultsGenerator
import com.klewerro.mitemperature2alt.coreTest.generators.ThermometerStatusGenerator
import com.klewerro.mitemperature2alt.domain.model.HourlyRecord
import com.klewerro.mitemperature2alt.domain.model.LastIndexTotalRecords
import com.klewerro.mitemperature2alt.domain.model.ThermometerScanResult
import com.klewerro.mitemperature2alt.temperatureSensor.ThermometerDeviceBleClient
import com.klewerro.mitemperature2alt.temperatureSensor.contracts.ThermometerDevicesBleScanner
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.nordicsemi.android.kotlin.ble.core.data.GattConnectionState

class FakeThermometerDevicesBleScanner : ThermometerDevicesBleScanner {
    var clientConnectionStateInternal = flowOf(GattConnectionState.STATE_CONNECTED)
    var isScanningInternal = MutableStateFlow(false)
    override val isScanning: StateFlow<Boolean> = isScanningInternal

    private val _bleDevices = MutableStateFlow<List<ThermometerScanResult>>(emptyList())
    override val bleDevices: StateFlow<List<ThermometerScanResult>> = _bleDevices

    var isScanCancelledFlag = false
    var scanResultDelay = 1_000L
    var isScanError = false
    var lastIndexTotalRecords = LastIndexTotalRecords(0, 0)
    var subscribeToThermometerHourlyRecordsThrowException: Boolean = false
    var readThermometerStatus = ThermometerStatusGenerator.thermometerStatus1
    var mac1 = ThermometerScanResultsGenerator.mac1
    var mac2 = ThermometerScanResultsGenerator.mac2
    private var scanResult1 = ThermometerScanResultsGenerator.scanResult1
    private var scanResult2 = ThermometerScanResultsGenerator.scanResult2

    var hourlyRecords = (0..15).map { i ->
        HourlyRecord(
            i,
            1,
            (10 + i).toFloat(),
            (20 + i).toFloat(),
            30 + i,
            50 + i
        )
    }.toTypedArray()

    override fun scanForDevices(coroutineScope: CoroutineScope): Job = coroutineScope.launch {
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

    override suspend fun connectToDevice(
        coroutineScope: CoroutineScope,
        bleDeviceAddress: String
    ): ThermometerDeviceBleClient {
        delay(scanResultDelay)
        return createThermometerBleDeviceBleClientMock()
    }

    override suspend fun scanAndConnect(
        coroutineScope: CoroutineScope,
        address: String
    ): ThermometerDeviceBleClient {
        delay(scanResultDelay)
        return if (isScanError) {
            throw IllegalStateException("STUB!")
        } else {
            createThermometerBleDeviceBleClientMock()
        }
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

    fun createThermometerBleDeviceBleClientMock(): ThermometerDeviceBleClient {
        val thermometerDeviceBleClientMock = mockk<ThermometerDeviceBleClient>()
        coEvery {
            thermometerDeviceBleClientMock.readThermometerStatus()
        } returns ThermometerStatusGenerator.thermometerStatus1
        coEvery {
            thermometerDeviceBleClientMock.connectionState
        } returns clientConnectionStateInternal

        coEvery {
            thermometerDeviceBleClientMock.readLastIndexAndTotalRecords()
        } returns lastIndexTotalRecords

        if (subscribeToThermometerHourlyRecordsThrowException) {
            coEvery {
                thermometerDeviceBleClientMock.subscribeToThermometerHourlyRecords()
            } returns flow {
                HourlyRecord(0, 1, 10.0f, 20.0f, 30, 50)
                HourlyRecord(1, 1, 10.0f, 20.0f, 30, 50)
                HourlyRecord(2, 1, 10.0f, 20.0f, 30, 50)
                throw Exception("Exception for testing purposes")
            }
        } else {
            coEvery {
                thermometerDeviceBleClientMock.subscribeToThermometerHourlyRecords()
            } returns flowOf(*hourlyRecords)
        }

        return thermometerDeviceBleClientMock
    }
}
