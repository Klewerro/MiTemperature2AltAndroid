package com.klewerro.mitemperature2alt.presentation.mainscreen

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.klewerro.mitemperature2alt.coreTest.fake.FakeThermometerRepository
import com.klewerro.mitemperature2alt.coreTest.util.MainCoroutineExtension
import com.klewerro.mitemperature2alt.coreTest.util.TestDispatchers
import com.klewerro.mitemperature2alt.domain.usecase.thermometer.scan.IsScanningForDevicesUseCase
import com.klewerro.mitemperature2alt.domain.usecase.thermometer.scan.ScanForDevicesUseCase
import com.klewerro.mitemperature2alt.domain.usecase.thermometer.scan.SearchedDevicesUseCase
import com.klewerro.mitemperature2alt.presentation.addHeater.DeviceSearchViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalCoroutinesApi::class)
class DeviceSearchViewModelTest {

    private lateinit var fakeThermometerRepository: FakeThermometerRepository
    private lateinit var searchedDevicesUseCase: SearchedDevicesUseCase
    private lateinit var isScanningForDevicesUseCase: IsScanningForDevicesUseCase
    private lateinit var scanForDevicesUseCase: ScanForDevicesUseCase
    private lateinit var deviceSearchViewModel: DeviceSearchViewModel

    companion object {
        @JvmField
        @RegisterExtension
        val mainCoroutineExtension = MainCoroutineExtension()
    }

    @BeforeEach
    fun setUp() {
        fakeThermometerRepository = FakeThermometerRepository()
        searchedDevicesUseCase = SearchedDevicesUseCase(fakeThermometerRepository)
        isScanningForDevicesUseCase = IsScanningForDevicesUseCase(fakeThermometerRepository)
        scanForDevicesUseCase = ScanForDevicesUseCase(fakeThermometerRepository)
        val testDispatchers = TestDispatchers(mainCoroutineExtension.testDispatcher)
        deviceSearchViewModel = DeviceSearchViewModel(
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
                    deviceSearchViewModel.scanForDevices()
                    val scanStartedItem = awaitItem()
                    assertThat(scanStartedItem).isTrue()
                }
                delay(2_000)
                val testJob2 = launch {
                    val resultItem = awaitItem()
                    assertThat(resultItem).isFalse()
                    deviceSearchViewModel.scanForDevices()
                    val scanStartedItem = awaitItem()
                    assertThat(scanStartedItem).isTrue()
                }
                delay(2_000)
                testJob2.cancel()
                advanceUntilIdle()
                val finalEmission = awaitItem()
                assertThat(finalEmission).isFalse()
            }
        }
}
