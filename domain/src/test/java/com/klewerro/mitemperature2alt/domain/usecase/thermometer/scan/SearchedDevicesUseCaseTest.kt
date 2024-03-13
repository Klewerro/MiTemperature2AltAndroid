package com.klewerro.mitemperature2alt.domain.usecase.thermometer.scan

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import com.klewerro.mitemperature2alt.coreTest.fake.FakeThermometerRepository
import com.klewerro.mitemperature2alt.coreTest.generators.ThermometerScanResultsGenerator
import com.klewerro.mitemperature2alt.coreTest.generators.ThermometerStatusGenerator
import com.klewerro.mitemperature2alt.domain.model.ConnectionStatus
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SearchedDevicesUseCaseTest {

    private lateinit var fakeThermometerRepository: FakeThermometerRepository
    private lateinit var searchedDevicesUseCase: SearchedDevicesUseCase

    @BeforeEach
    fun setUp() {
        fakeThermometerRepository = FakeThermometerRepository()
        searchedDevicesUseCase = SearchedDevicesUseCase(fakeThermometerRepository)
    }

    @Test
    fun `invoke when scan for devices not started emits empty flow`() = runBlocking {
        searchedDevicesUseCase().test {
            val item1 = awaitItem()
            assertThat(item1).isEmpty()
        }
    }

    @Test
    fun `invoke when all scanned devices has not connected status don't modify rssi and connection status`() =
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
                        thermometerScanResult.connectionStatus
                    ).isEqualTo(ConnectionStatus.NOT_CONNECTED)
                }
            }
        }

    @Test
    fun `invoke when connectingToDeviceAddress is equal to one item, change it's ConnectionStatus to CONNECTING`() =
        runBlocking {
            searchedDevicesUseCase().test {
                awaitItem()
                val scanResults = ThermometerScanResultsGenerator.listOfScanResults
                val address = scanResults[1].address
                fakeThermometerRepository.scannedDevicesInternal.update {
                    scanResults
                }
                val scanResultsItem = awaitItem()
                fakeThermometerRepository.connectingToDeviceAddressInternal.update {
                    address
                }

                val scanResultsItemWithConnectingAddressItem = awaitItem()
                assertThat(
                    scanResultsItemWithConnectingAddressItem[0].connectionStatus
                ).isEqualTo(ConnectionStatus.NOT_CONNECTED)
                assertThat(
                    scanResultsItemWithConnectingAddressItem[1].connectionStatus
                ).isEqualTo(ConnectionStatus.CONNECTING)
            }
        }

    @Test
    fun `invoke when connectedDevicesStatuses address is equal to one scan result set connection status to CONNECTED`() =
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
                    scanResultsWithConnectedDevicesStatuses[0].connectionStatus
                ).isEqualTo(ConnectionStatus.CONNECTED)
                assertThat(
                    scanResultsWithConnectedDevicesStatuses[1].connectionStatus
                ).isEqualTo(ConnectionStatus.NOT_CONNECTED)
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
    fun `invoke when connectingToDeviceAddress and connectedDevicesStatuses are both equal to different scanResults, set connectionStatus CONNECTING and CONNECTED`() =
        runBlocking {
            searchedDevicesUseCase().test {
                awaitItem()
                val scanResults = ThermometerScanResultsGenerator.listOfScanResults
                val address = scanResults[0].address
                fakeThermometerRepository.scannedDevicesInternal.update {
                    scanResults
                }
                val scanResultsItem = awaitItem()

                fakeThermometerRepository.connectingToDeviceAddressInternal.update {
                    address
                }
                val scanResultsItemWithConnectingAddressItem = awaitItem()

                val thermometerStatus = ThermometerStatusGenerator.thermometerStatus1
                fakeThermometerRepository.connectedDevicesStatusesInternal.update {
                    it.plus(
                        scanResults[1].address to thermometerStatus
                    )
                }

                val resultItem = awaitItem()

                assertThat(resultItem[0].connectionStatus).isEqualTo(ConnectionStatus.CONNECTING)
                assertThat(resultItem[1].connectionStatus).isEqualTo(ConnectionStatus.CONNECTED)
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
}
