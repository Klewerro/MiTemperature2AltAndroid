package com.klewerro.mitemperature2alt.presentation.mainscreen

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isNotNull
import com.klewerro.mitemperature2alt.coreTest.fake.FakePersistenceRepository
import com.klewerro.mitemperature2alt.coreTest.fake.FakeThermometerRepository
import com.klewerro.mitemperature2alt.coreTest.util.MainCoroutineExtension
import com.klewerro.mitemperature2alt.coreTest.util.TestDispatchers
import com.klewerro.mitemperature2alt.domain.model.ConnectionStatus
import com.klewerro.mitemperature2alt.domain.model.ThermometerScanResult
import com.klewerro.mitemperature2alt.domain.usecase.thermometer.connect.ConnectToDeviceUseCase
import com.klewerro.mitemperature2alt.domain.usecase.thermometer.connect.ConnectedDevicesUseCase
import com.klewerro.mitemperature2alt.domain.usecase.thermometer.operations.ReadCurrentThermometerStatusUseCase
import com.klewerro.mitemperature2alt.domain.usecase.thermometer.operations.SubscribeToCurrentThermometerStatusUseCase
import com.klewerro.mitemperature2alt.domain.usecase.thermometer.persistence.SaveThermometerUseCase
import com.klewerro.mitemperature2alt.domain.usecase.thermometer.persistence.SavedThermometersUseCase
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class BleOperationsViewModelTest {

    private lateinit var fakeThermometerRepository: FakeThermometerRepository
    private lateinit var fakePersistenceRepository: FakePersistenceRepository
    private lateinit var connectedDevicesUseCase: ConnectedDevicesUseCase
    private lateinit var connectToDeviceUseCase: ConnectToDeviceUseCase
    private lateinit var readCurrentThermometerStatusUseCase: ReadCurrentThermometerStatusUseCase
    private lateinit var subscribeToCurrentThermometerStatusUseCase:
        SubscribeToCurrentThermometerStatusUseCase
    private lateinit var savedThermometersUseCase: SavedThermometersUseCase
    private lateinit var saveThermometerUseCase: SaveThermometerUseCase
    private lateinit var bleOperationsViewModel: BleOperationsViewModel

    companion object {
        @JvmField
        @RegisterExtension
        val mainCoroutineExtension = MainCoroutineExtension()
    }

    @BeforeEach
    fun setUp() {
        fakeThermometerRepository = FakeThermometerRepository()
        fakePersistenceRepository = FakePersistenceRepository()
        connectedDevicesUseCase = ConnectedDevicesUseCase(fakeThermometerRepository)
        connectToDeviceUseCase = ConnectToDeviceUseCase(fakeThermometerRepository)
        readCurrentThermometerStatusUseCase = ReadCurrentThermometerStatusUseCase(
            fakeThermometerRepository
        )
        subscribeToCurrentThermometerStatusUseCase = SubscribeToCurrentThermometerStatusUseCase(
            fakeThermometerRepository
        )
        savedThermometersUseCase = SavedThermometersUseCase(fakePersistenceRepository)
        saveThermometerUseCase = SaveThermometerUseCase(fakePersistenceRepository)
        bleOperationsViewModel = BleOperationsViewModel(
            connectedDevicesUseCase = connectedDevicesUseCase,
            connectToDeviceUseCase = connectToDeviceUseCase,
            readCurrentThermometerStatusUseCase = readCurrentThermometerStatusUseCase,
            subscribeToCurrentThermometerStatusUseCase = subscribeToCurrentThermometerStatusUseCase,
            savedThermometersUseCase = savedThermometersUseCase,
            saveThermometerUseCase = saveThermometerUseCase,
            dispatchers = TestDispatchers(mainCoroutineExtension.testDispatcher)
        )
    }

    @Test
    fun `ConnectToDevice event when IllegalStateException thrown set state to error`() = runTest {
        val scanResult = ThermometerScanResult(
            "name",
            "00:00:00:00",
            -100,
            ConnectionStatus.NOT_CONNECTED
        )
        bleOperationsViewModel.state.test {
            awaitItem()
            fakeThermometerRepository.connectingToDeviceAddressInternal.update { "00:00:00:01" }
            bleOperationsViewModel.onEvent(BleOperationsEvent.ConnectToDevice(scanResult))
            val errorSate = awaitItem()

            assertThat(errorSate.error).isNotNull()
        }
    }

    @Test
    fun `SaveThermometer event when address is null set state to error`() = runTest {
        bleOperationsViewModel.state.test {
            awaitItem()
            // Save thermometer without previous setting name
            bleOperationsViewModel.onEvent(BleOperationsEvent.SaveThermometer("Test"))
            val errorState = awaitItem()
            assertThat(errorState.error).isNotNull()
        }
    }
}
