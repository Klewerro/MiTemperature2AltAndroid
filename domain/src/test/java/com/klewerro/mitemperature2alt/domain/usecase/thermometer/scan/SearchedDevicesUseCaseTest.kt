package com.klewerro.mitemperature2alt.domain.usecase.thermometer.scan

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import com.klewerro.mitemperature2alt.coreTest.fake.FakePersistenceRepository
import com.klewerro.mitemperature2alt.coreTest.fake.FakeThermometerRepository
import com.klewerro.mitemperature2alt.coreTest.generators.ThermometerScanResultsGenerator
import com.klewerro.mitemperature2alt.coreTest.generators.ThermometerStatusGenerator
import com.klewerro.mitemperature2alt.domain.model.ScannedDeviceStatus
import com.klewerro.mitemperature2alt.domain.repository.PersistenceRepository
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SearchedDevicesUseCaseTest {

    private lateinit var fakeThermometerRepository: FakeThermometerRepository
    private lateinit var fakePersistenceRepository: PersistenceRepository
    private lateinit var searchedDevicesUseCase: SearchedDevicesUseCase

    @BeforeEach
    fun setUp() {
        fakeThermometerRepository = FakeThermometerRepository()
        fakePersistenceRepository = FakePersistenceRepository()
        searchedDevicesUseCase = SearchedDevicesUseCase(
            fakeThermometerRepository,
            fakePersistenceRepository
        )
    }

    @Test
    fun `invoke when scan for devices not started emits empty flow`() = runBlocking {
        searchedDevicesUseCase().test {
            val item1 = awaitItem()
            assertThat(item1).isEmpty()
        }
    }

    @Test
    fun `invoke when all scanned devices has not connected status don't modify rssi and scannedDeviceStatus`() =
        runBlocking {
            searchedDevicesUseCase().test {
                awaitItem()
                val scanResults = ThermometerScanResultsGenerator.listOfScanResults
                fakeThermometerRepository.scannedDevicesInternal.update {
                    scanResults
                }

                val item1 = awaitItem()
                item1.forEachIndexed { index, thermometerScanResult ->
                    assertThat(thermometerScanResult.rssi).isEqualTo(scanResults[index].rssi)
                    assertThat(
                        thermometerScanResult.scannedDeviceStatus
                    ).isEqualTo(ScannedDeviceStatus.NOT_CONNECTED)
                }
            }
        }

    @Test
    fun `invoke when connectedDevicesStatuses address is equal to one scan result set scannedDeviceStatus to CONNECTED`() =
        runBlocking {
            searchedDevicesUseCase().test {
                awaitItem()
                val scanResults = ThermometerScanResultsGenerator.listOfScanResults
                fakeThermometerRepository.scannedDevicesInternal.update {
                    scanResults
                }
                val scanResultsItem = awaitItem()
                val thermometerStatus = ThermometerStatusGenerator.thermometerStatus1
                fakeThermometerRepository.connectedDevicesStatusesInternal.update {
                    it.plus(
                        scanResults[0].address to thermometerStatus
                    )
                }

                val scanResultsWithConnectedDevicesStatuses = awaitItem()
                assertThat(
                    scanResultsWithConnectedDevicesStatuses[0].scannedDeviceStatus
                ).isEqualTo(ScannedDeviceStatus.CONNECTED)
                assertThat(
                    scanResultsWithConnectedDevicesStatuses[1].scannedDeviceStatus
                ).isEqualTo(ScannedDeviceStatus.NOT_CONNECTED)
            }
        }

    @Test
    fun `invoke when connectedDevicesStatuses address is equal to one scan result keep scanResult rssi, because rssiStrengths is not emitted`() =
        runBlocking {
            searchedDevicesUseCase().test {
                awaitItem()
                val scanResults = ThermometerScanResultsGenerator.listOfScanResults
                fakeThermometerRepository.scannedDevicesInternal.update {
                    scanResults
                }
                val scanResultsItem = awaitItem()
                val thermometerStatus = ThermometerStatusGenerator.thermometerStatus1
                fakeThermometerRepository.connectedDevicesStatusesInternal.update {
                    it.plus(
                        scanResults[0].address to thermometerStatus
                    )
                }

                val scanResultsWithConnectedDevicesStatuses = awaitItem()
                assertThat(
                    scanResultsWithConnectedDevicesStatuses[0].rssi
                ).isEqualTo(scanResults[0].rssi)
                assertThat(
                    scanResultsWithConnectedDevicesStatuses[1].rssi
                ).isEqualTo(scanResults[1].rssi)
            }
        }

    @Test
    fun `invoke when rssiStrengths isEqual to connected scanResult, set final rssi to rssiStrengths`() =
        runBlocking {
            searchedDevicesUseCase().test {
                awaitItem()
                val scanResults = ThermometerScanResultsGenerator.listOfScanResults
                fakeThermometerRepository.scannedDevicesInternal.update {
                    scanResults
                }

                val thermometerStatus = ThermometerStatusGenerator.thermometerStatus1
                fakeThermometerRepository.connectedDevicesStatusesInternal.update {
                    it.plus(
                        scanResults[0].address to thermometerStatus
                    )
                }
                awaitItem()

                fakeThermometerRepository.rssiStrengthsInternal.update {
                    it.plus(
                        scanResults[0].address to -999 // Obvious custom value
                    )
                }
                awaitItem()
                val resultItem = awaitItem()

                assertThat(resultItem[0].rssi).isEqualTo(-999)
                assertThat(resultItem[1].rssi).isEqualTo(scanResults[1].rssi)
            }
        }

    @Test
    fun `invoke when device is saved and not connected set scannedDeviceStatus to SAVED`() =
        runBlocking {
            searchedDevicesUseCase().test {
                awaitItem()
                val scanResults = ThermometerScanResultsGenerator.listOfScanResults
                fakeThermometerRepository.scannedDevicesInternal.update {
                    scanResults
                }
                val searchedDevices1 = awaitItem()

                fakePersistenceRepository.saveThermometer("thermometer1", scanResults[0].address)
                val searchedDevices2 = awaitItem()

                assertThat(
                    searchedDevices1[0].scannedDeviceStatus == ScannedDeviceStatus.NOT_CONNECTED
                )
                assertThat(searchedDevices2[0].scannedDeviceStatus == ScannedDeviceStatus.SAVED)
            }
        }

    @Test
    fun `invoke when device is saved and connected set scannedDeviceStatus to SAVED`() =
        runBlocking {
            searchedDevicesUseCase().test {
                awaitItem()
                val scanResults = listOf(
                    ThermometerScanResultsGenerator.scanResult2.copy(
                        scannedDeviceStatus = ScannedDeviceStatus.CONNECTED
                    ),
                    ThermometerScanResultsGenerator.scanResult2
                )
                fakeThermometerRepository.scannedDevicesInternal.update {
                    scanResults
                }
                val searchedDevices1 = awaitItem()

                fakePersistenceRepository.saveThermometer("thermometer1", scanResults[0].address)
                val searchedDevices2 = awaitItem()

                assertThat(
                    searchedDevices1[0].scannedDeviceStatus == ScannedDeviceStatus.CONNECTED
                )
                assertThat(searchedDevices2[0].scannedDeviceStatus == ScannedDeviceStatus.SAVED)
            }
        }
}
