package com.klewerro.mitemperature2alt.presentation.mainscreen

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isInstanceOf
import com.klewerro.mitemperature2alt.coreTest.fake.FakeThermometerRepository
import com.klewerro.mitemperature2alt.coreTest.generators.ThermometerScanResultsGenerator
import com.klewerro.mitemperature2alt.coreTest.util.MainCoroutineExtension
import com.klewerro.mitemperature2alt.coreTest.util.TestDispatchers
import com.klewerro.mitemperature2alt.domain.usecase.thermometer.connect.ConnectToDeviceUseCase
import com.klewerro.mitemperature2alt.domain.usecase.thermometer.connect.ConnectedDevicesUseCase
import com.klewerro.mitemperature2alt.domain.usecase.thermometer.operations.ReadCurrentThermometerStatusUseCase
import com.klewerro.mitemperature2alt.domain.usecase.thermometer.operations.SubscribeToCurrentThermometerStatusUseCase
import com.klewerro.mitemperature2alt.presentation.util.UiText
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class BleOperationsViewModelTest {

    private lateinit var fakeThermometerRepository: FakeThermometerRepository
    private lateinit var connectedDevicesUseCase: ConnectedDevicesUseCase
    private lateinit var connectToDeviceUseCase: ConnectToDeviceUseCase
    private lateinit var readCurrentThermometerStatusUseCase: ReadCurrentThermometerStatusUseCase
    private lateinit var subscribeToCurrentThermometerStatusUseCase:
        SubscribeToCurrentThermometerStatusUseCase
    private lateinit var bleOperationsViewModel: BleOperationsViewModel

    companion object {
        @JvmField
        @RegisterExtension
        val mainCoroutineExtension = MainCoroutineExtension()
    }

    @BeforeEach
    fun setUp() {
        fakeThermometerRepository = FakeThermometerRepository()
        connectedDevicesUseCase = ConnectedDevicesUseCase(fakeThermometerRepository)
        connectToDeviceUseCase = ConnectToDeviceUseCase(fakeThermometerRepository)
        readCurrentThermometerStatusUseCase = ReadCurrentThermometerStatusUseCase(
            fakeThermometerRepository
        )
        subscribeToCurrentThermometerStatusUseCase = SubscribeToCurrentThermometerStatusUseCase(
            fakeThermometerRepository
        )
        bleOperationsViewModel = BleOperationsViewModel(
            connectedDevicesUseCase = connectedDevicesUseCase,
            connectToDeviceUseCase = connectToDeviceUseCase,
            readCurrentThermometerStatusUseCase = readCurrentThermometerStatusUseCase,
            subscribeToCurrentThermometerStatusUseCase = subscribeToCurrentThermometerStatusUseCase,
            dispatchers = TestDispatchers(mainCoroutineExtension.testDispatcher)
        )
    }

    /**
     * Called when already connecting to the same/different device
     */
    @Test
    fun `connectToDevice called twice will send uiTextError`() = runTest {
        bleOperationsViewModel.uiTextError.test {
            bleOperationsViewModel.connectToDevice(ThermometerScanResultsGenerator.scanResult1)
            bleOperationsViewModel.connectToDevice(ThermometerScanResultsGenerator.scanResult2)
            val uiTextItem = awaitItem()
            uiTextItem.toString()
            assertThat(uiTextItem).isInstanceOf(UiText.StringResource::class)
        }
    }
}
