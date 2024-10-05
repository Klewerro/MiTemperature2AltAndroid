package com.klewerro.mitemperature2alt.addThermometerPresentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import assertk.assertions.isTrue
import com.klewerro.mitemperature2alt.addThermometerDomain.usecase.ConnectToDeviceUseCase
import com.klewerro.mitemperature2alt.coreTest.fake.FakePersistenceRepository
import com.klewerro.mitemperature2alt.coreTest.fake.FakeThermometerRepository
import com.klewerro.mitemperature2alt.coreTest.util.MainCoroutineExtension
import com.klewerro.mitemperature2alt.coreTest.util.TestDispatchers
import com.klewerro.mitemperature2alt.coreUi.UiConstants
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class ConnectThermometerViewModelTest {

    private lateinit var fakeThermometerRepository: FakeThermometerRepository
    private lateinit var fakePersistenceRepository: FakePersistenceRepository
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var connectToDeviceUseCase: ConnectToDeviceUseCase
    private lateinit var connectThermometerViewModel: ConnectThermometerViewModel

    private val deviceAddress = "00:00:00:00:01"

    companion object {
        @JvmField
        @RegisterExtension
        val mainCoroutineExtension = MainCoroutineExtension()
    }

    @BeforeEach
    fun setUp() {
        val testDispatchers = TestDispatchers(mainCoroutineExtension.testDispatcher)
        fakeThermometerRepository = FakeThermometerRepository()
        fakePersistenceRepository = FakePersistenceRepository()

        savedStateHandle = SavedStateHandle(
            initialState = mapOf(
                UiConstants.NAV_PARAM_ADDRESS to deviceAddress
            )
        )
        connectToDeviceUseCase = ConnectToDeviceUseCase(fakeThermometerRepository)

        connectThermometerViewModel = ConnectThermometerViewModel(
            savedState = savedStateHandle,
            persistenceRepository = fakePersistenceRepository,
            thermometerRepository = fakeThermometerRepository,
            connectToDeviceUseCase = connectToDeviceUseCase,
            dispatchers = testDispatchers
        )
    }

    @Test
    fun `ConnectToDevice event when connectToDeviceUseCase not throwing exception updating viewModel state correctly`() {
        val resultThermometerStatus = fakeThermometerRepository.thermometerStatus
        runTest {
            connectThermometerViewModel.state.test {
                val initialState = awaitItem()
                val addressState = awaitItem()

                connectThermometerViewModel.onEvent(
                    ConnectThermometerEvent.ConnectToDevice(
                        connectThermometerViewModel.viewModelScope
                    )
                )
                val statusConnectingState = awaitItem()
                val stateAfterSuccessConnection = awaitItem()

                connectThermometerViewModel.viewModelScope.cancel(
                    "Cancel for test purposes. Need to be cancelled, " +
                        "because otherwise job won't end (because observing thermometer status)."
                )

                initialState.toString()
                statusConnectingState.toString()
                stateAfterSuccessConnection.toString()

                assertThat(initialState.connectingStatus).isEqualTo(ConnectingStatus.NOT_CONNECTING)
                assertThat(initialState.thermometerAddress).isEqualTo("")
                assertThat(addressState.thermometerAddress).isEqualTo(deviceAddress)
                assertThat(
                    statusConnectingState.connectingStatus
                ).isEqualTo(ConnectingStatus.CONNECTING)
                assertThat(
                    stateAfterSuccessConnection.connectingStatus
                ).isEqualTo(ConnectingStatus.CONNECTED)
                assertThat(
                    stateAfterSuccessConnection.connectThermometerStatus
                ).isEqualTo(resultThermometerStatus)
            }
        }
    }

    @Test
    fun `ConnectToDevice event when connectToDeviceUseCase throwing IllegalStateException updating viewModel state correctly`() {
        fakeThermometerRepository.isConnectToDeviceThrowingError = true
        runTest {
            connectThermometerViewModel.state.test {
                awaitItem() // Initial state emission
                val addressState = awaitItem()

                connectThermometerViewModel.onEvent(
                    ConnectThermometerEvent.ConnectToDevice(
                        connectThermometerViewModel.viewModelScope
                    )
                )
                val statusConnectingState = awaitItem()
                val stateAfterConnectingError = awaitItem()

                assertThat(addressState.connectingStatus).isEqualTo(ConnectingStatus.NOT_CONNECTING)
                assertThat(addressState.error).isNull()
                assertThat(
                    statusConnectingState.connectingStatus
                ).isEqualTo(ConnectingStatus.CONNECTING)
                assertThat(
                    stateAfterConnectingError.connectingStatus
                ).isEqualTo(ConnectingStatus.ERROR)
                assertThat(stateAfterConnectingError.error).isNotNull()
            }
        }
    }

    @Test
    fun `SaveThermometer event when name is not blank set state thermometerSaved to true`() =
        runTest {
            val name = "test"
            connectThermometerViewModel.state.test {
                val initialState = awaitItem()
                connectThermometerViewModel.onEvent(
                    ConnectThermometerEvent.ChangeThermometerName(name)
                )
                val nameUpdatedState = awaitItem()

                connectThermometerViewModel.onEvent(ConnectThermometerEvent.SaveThermometer)
                val saveResultState = awaitItem()

                assertThat(initialState.thermometerSaved).isFalse()
                assertThat(nameUpdatedState.thermometerName).isEqualTo(name)
                assertThat(saveResultState.thermometerSaved).isTrue()
            }
        }

    @Test
    fun `SaveThermometer event when name is blank set error state`() = runTest {
        connectThermometerViewModel.state.test {
            val initialState = awaitItem()

            connectThermometerViewModel.onEvent(ConnectThermometerEvent.SaveThermometer)
            val saveErrorState = awaitItem()

            assertThat(initialState.thermometerSaved).isFalse()
            assertThat(initialState.error).isNull()
            assertThat(saveErrorState.error).isNotNull()
            assertThat(saveErrorState.thermometerSaved).isFalse()
        }
    }

    @Test
    fun `SaveThermometer and then ChangeThermometerName event name is empty and changeThermometerName is not, set state error and then to updated name and clear error`() {
        val newName = "t"
        runTest {
            connectThermometerViewModel.state.test {
                val initialState = awaitItem()
                connectThermometerViewModel.onEvent(ConnectThermometerEvent.SaveThermometer)
                val saveErrorState = awaitItem()

                connectThermometerViewModel.onEvent(
                    ConnectThermometerEvent.ChangeThermometerName(newName)
                )
                val nameUpdatedState = awaitItem()

                assertThat(initialState.thermometerName).isEqualTo("")
                assertThat(initialState.error).isNull()
                assertThat(saveErrorState.error).isNotNull()

                assertThat(nameUpdatedState.thermometerName).isEqualTo(newName)
                assertThat(nameUpdatedState.error).isNull()
            }
        }
    }

    @Test
    fun `SaveThermometer and then ChangeThermometerName event name is empty and changeThermometerName is whitespace, set state error and after name change keeps it`() {
        val newName = " "
        runTest {
            connectThermometerViewModel.state.test {
                val initialState = awaitItem()
                connectThermometerViewModel.onEvent(ConnectThermometerEvent.SaveThermometer)
                val saveErrorState = awaitItem()

                connectThermometerViewModel.onEvent(
                    ConnectThermometerEvent.ChangeThermometerName(newName)
                )
                val nameChangedEvent = awaitItem()

                assertThat(initialState.thermometerName).isEqualTo("")
                assertThat(initialState.error).isNull()
                assertThat(saveErrorState.error).isNotNull()
                assertThat(nameChangedEvent.error).isNotNull()
            }
        }
    }
}
