package com.klewerro.mitemperature2alt.addThermometerPresentation.search

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.klewerro.mitemperature2alt.coreTest.fake.FakePersistenceRepository
import com.klewerro.mitemperature2alt.coreTest.fake.FakeThermometerRepository
import com.klewerro.mitemperature2alt.coreTest.util.MainCoroutineExtension
import com.klewerro.mitemperature2alt.coreTest.util.TestDispatchers
import com.klewerro.mitemperature2alt.domain.usecase.thermometer.scan.IsScanningForDevicesUseCase
import com.klewerro.mitemperature2alt.domain.usecase.thermometer.scan.ScanForDevicesUseCase
import com.klewerro.mitemperature2alt.domain.usecase.thermometer.scan.SearchedDevicesUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalCoroutinesApi::class)
class DeviceSearchViewModelTest {

    private lateinit var fakeThermometerRepository: FakeThermometerRepository
    private lateinit var fakePersistenceRepository: FakePersistenceRepository
    private lateinit var searchedDevicesUseCase: SearchedDevicesUseCase
    private lateinit var isScanningForDevicesUseCase: IsScanningForDevicesUseCase
    private lateinit var scanForDevicesUseCase: ScanForDevicesUseCase
    private lateinit var deviceSearchViewModel:
        com.klewerro.mitemperature2alt.addThermometerPresentation.search.DeviceSearchViewModel

    companion object {
        @JvmField
        @RegisterExtension
        val mainCoroutineExtension = MainCoroutineExtension()
    }

    @BeforeEach
    fun setUp() {
        fakeThermometerRepository = FakeThermometerRepository()
        fakePersistenceRepository = FakePersistenceRepository()
        searchedDevicesUseCase = SearchedDevicesUseCase(
            fakeThermometerRepository,
            fakePersistenceRepository
        )
        isScanningForDevicesUseCase = IsScanningForDevicesUseCase(fakeThermometerRepository)
        scanForDevicesUseCase = ScanForDevicesUseCase(fakeThermometerRepository)
        val testDispatchers = TestDispatchers(mainCoroutineExtension.testDispatcher)
        deviceSearchViewModel =
            com.klewerro.mitemperature2alt.addThermometerPresentation.search.DeviceSearchViewModel(
                searchedDevicesUseCase = searchedDevicesUseCase,
                isScanningForDevicesUseCase = isScanningForDevicesUseCase,
                scanForDevicesUseCase = scanForDevicesUseCase,
                dispatchers = testDispatchers
            )
    }

    @RepeatedTest(10) // Repeated, because it can easily became flaky
    fun `scanForDevices when scan is already scanning stopping current job and starting new one`() =
        runTest {
            isScanningForDevicesUseCase().test {
                assertThat(awaitItem()).isFalse()
                val testJob = launch {
                    deviceSearchViewModel.onEvent(
                        com.klewerro.mitemperature2alt.addThermometerPresentation.search.DeviceSearchEvent.ScanForDevices()
                    )
                    val scanStartedItem = awaitItem()
                    assertThat(scanStartedItem).isTrue()
                }
                delay(2_000)
                val testJob2 = launch {
                    val resultItem = awaitItem()
                    assertThat(resultItem).isFalse()
                    deviceSearchViewModel.onEvent(
                        com.klewerro.mitemperature2alt.addThermometerPresentation.search.DeviceSearchEvent.ScanForDevices()
                    )
                    val scanStartedItem = awaitItem()
                    assertThat(scanStartedItem).isTrue()
                }
                delay(2_000)
                testJob2.cancel()
                deviceSearchViewModel.onEvent(
                    com.klewerro.mitemperature2alt.addThermometerPresentation.search.DeviceSearchEvent.StopScanForDevices()
                )
                advanceUntilIdle()
                val finalEmission = awaitItem()
                assertThat(finalEmission).isFalse()
            }
        }

    @Test
    fun `handleScanForDevices when started by user starting scan for devices`() = runTest {
        deviceSearchViewModel.state.test {
            val item1 = awaitItem()
            item1.toString()

            deviceSearchViewModel.onEvent(
                com.klewerro.mitemperature2alt.addThermometerPresentation.search.DeviceSearchEvent.ScanForDevices(
                    byUser = true
                )
            )
            val scanningState = awaitItem()
            scanningState.toString()
            assertThat(scanningState.isScanningForDevices).isTrue()

            delay(2_000)
            deviceSearchViewModel.onEvent(
                com.klewerro.mitemperature2alt.addThermometerPresentation.search.DeviceSearchEvent.StopScanForDevices(
                    byUser = true
                )
            )
            awaitItem() // scanResult item added to state
            val scanningEndState = awaitItem()
            assertThat(scanningEndState.isScanningForDevices).isFalse()
        }
    }

    @Test
    fun `handleScanForDevices when stopped stan bu user and startednot  by user not starting scan for devices`() =
        runTest {
            deviceSearchViewModel.state.test {
                val item1 = awaitItem()

                deviceSearchViewModel.onEvent(
                    com.klewerro.mitemperature2alt.addThermometerPresentation.search.DeviceSearchEvent.StopScanForDevices(
                        byUser = true
                    )
                )

                deviceSearchViewModel.onEvent(
                    com.klewerro.mitemperature2alt.addThermometerPresentation.search.DeviceSearchEvent.ScanForDevices(
                        byUser = false
                    )
                )
                this.expectNoEvents()
            }
        }
}
