package com.klewerro.mitemperature2alt.presentation.mainscreen

import androidx.lifecycle.viewModelScope
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import com.klewerro.mitemperature2alt.coreTest.fake.FakeHourlyRecordRepository
import com.klewerro.mitemperature2alt.coreTest.fake.FakePersistenceRepository
import com.klewerro.mitemperature2alt.coreTest.fake.FakeThermometerRepository
import com.klewerro.mitemperature2alt.coreTest.generators.ThermometerGenerator
import com.klewerro.mitemperature2alt.coreTest.util.MainCoroutineExtension
import com.klewerro.mitemperature2alt.coreTest.util.TestDispatchers
import com.klewerro.mitemperature2alt.domain.usecase.GetHourlyResultsUseCase
import com.klewerro.mitemperature2alt.domain.usecase.ScanAndConnectToDeviceUseCase
import com.klewerro.mitemperature2alt.domain.usecase.ThermometerListUseCase
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class BleOperationsViewModelTest {
    private lateinit var fakeThermometerRepository: FakeThermometerRepository
    private lateinit var fakePersistenceRepository: FakePersistenceRepository
    private lateinit var fakeHourlyRecordRepository: FakeHourlyRecordRepository

    private lateinit var thermometerListUseCase: ThermometerListUseCase
    private lateinit var scanAndConnectToDeviceUseCase: ScanAndConnectToDeviceUseCase
    private lateinit var getHourlyResultsUseCase: GetHourlyResultsUseCase

    private lateinit var bleOperationsViewModel: BleOperationsViewModel

    private val connectThermometer = ThermometerGenerator.thermometer1

    @BeforeEach
    fun setUp() {
        val testDispatchers = TestDispatchers(mainCoroutineExtension.testDispatcher)
        fakeThermometerRepository = FakeThermometerRepository()
        fakePersistenceRepository = FakePersistenceRepository()
        fakeHourlyRecordRepository = FakeHourlyRecordRepository()
        scanAndConnectToDeviceUseCase =
            ScanAndConnectToDeviceUseCase(this.fakeThermometerRepository)
        thermometerListUseCase = ThermometerListUseCase(
            persistenceRepository = fakePersistenceRepository,
            thermometerRepository = fakeThermometerRepository
        )
        getHourlyResultsUseCase = GetHourlyResultsUseCase(
            thermometerRepository = fakeThermometerRepository,
            hourlyRecordRepository = fakeHourlyRecordRepository
        )
        bleOperationsViewModel = BleOperationsViewModel(
            thermometerListUseCase = thermometerListUseCase,
            thermometerRepository = fakeThermometerRepository,
            scanAndConnectToDeviceUseCase = scanAndConnectToDeviceUseCase,
            getHourlyResultsUseCase = getHourlyResultsUseCase,
            dispatchers = testDispatchers
        )
    }

    companion object {
        @JvmField
        @RegisterExtension
        val mainCoroutineExtension = MainCoroutineExtension()
    }

    @Test
    fun `ConnectToDevice event when scanAndConnectUseCase throw IllegalStateException set error state and Idle operation type`() {
        fakeThermometerRepository.isConnectToDeviceThrowingError = true
        runTest {
            bleOperationsViewModel.state.test {
                awaitItem()
                bleOperationsViewModel.onEvent(
                    BleOperationsEvent.ConnectToDevice(connectThermometer)
                )
                val connectingState = awaitItem()
                val idleErrorState = awaitItem()

                assertThat(connectingState.error).isNull()
                assertThat(
                    connectingState.thermometerOperationType
                ).isInstanceOf(ThermometerOperationType.ConnectingToDevice::class)

                assertThat(idleErrorState.error).isNotNull()
                assertThat(
                    idleErrorState.thermometerOperationType
                ).isEqualTo(ThermometerOperationType.Idle)
            }
        }
    }

    @Test
    fun `ConnectToDevice event when scanAndConnectUseCase successfully connect set state to connecting and idle without error`() {
        runTest {
            bleOperationsViewModel.state.test {
                awaitItem()
                bleOperationsViewModel.onEvent(
                    BleOperationsEvent.ConnectToDevice(connectThermometer)
                )
                val connectingState = awaitItem()
                val idleState = awaitItem() // Nie przychodzi...

                bleOperationsViewModel.viewModelScope.cancel(
                    "Cancel for test purposes. Need to be cancelled, " +
                        "because otherwise job won't end (because observing thermometer status)."
                )
                assertThat(connectingState.error).isNull()
                assertThat(
                    connectingState.thermometerOperationType
                ).isInstanceOf(ThermometerOperationType.ConnectingToDevice::class)

                assertThat(idleState.error).isNull()
                assertThat(
                    idleState.thermometerOperationType
                ).isEqualTo(ThermometerOperationType.Idle)
            }
        }
    }

    @Test
    fun `Disconnect event cancel job`() {
        runTest {
            bleOperationsViewModel.state.test {
                val initialState = awaitItem()
                bleOperationsViewModel.onEvent(
                    BleOperationsEvent.ConnectToDevice(connectThermometer)
                )
                val connectingState = awaitItem()
                val idleState = awaitItem()

                bleOperationsViewModel.onEvent(BleOperationsEvent.Disconnect(connectThermometer))
                // If test is ended correctly (without stuck), then thermometer job is cancelled properly
            }
        }
    }
}
