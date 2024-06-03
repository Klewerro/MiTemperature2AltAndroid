package com.klewerro.mitemperature2alt.presentation.mainscreen

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.klewerro.mitemperature2alt.MainActivity
import com.klewerro.mitemperature2alt.MiTemperature2AltAndroidTest
import com.klewerro.mitemperature2alt.coreTest.fake.FakeThermometerRepository
import com.klewerro.mitemperature2alt.coreTest.generators.ThermometerScanResultsGenerator
import com.klewerro.mitemperature2alt.domain.model.ThermometerConnectionStatus
import com.klewerro.mitemperature2alt.domain.repository.ThermometerRepository
import com.klewerro.mitemperature2alt.persistence.entity.ThermometerEntity
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class MainScreenTest : MiTemperature2AltAndroidTest() {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var thermometerRepository: ThermometerRepository

    private val scanResult = ThermometerScanResultsGenerator.scanResult1

    @Before
    override fun setUp() {
        super.setUp()
        GlobalScope.launch {
            db.thermometerDao.insertThermometer(
                ThermometerEntity(scanResult.address, scanResult.name)
            )
        }
    }

    @Test
    fun whenSavedThermometerExist_onConnectThermometerClick_showsConnectingTextAndProgressBar() =
        runBlocking<Unit> {
            MainScreenRobot(context, composeRule)
                .performClickOnConnectButton()
                .waitUntilConnectingTextExists()
                .assertConnectingTextIsDisplayed()
                .assertProgressBarVisibility(isDisplayed = true)
        }

    @Test
    fun whenSavedThermometerExist_onConnectThermometerClick_whenConnectSuccessfully_progressBarAndAddressDisappearAndTemperatureIsShown() =
        runBlocking<Unit> {
            MainScreenRobot(context, composeRule)
                .performClickOnConnectButton()
                .waitUntilCelsiusDegreesTextExists()
                .assertProgressBarVisibility(isDisplayed = false)
                .assertCelsiusDegreesTextVisibility(isDisplayed = true)
        }

    @Test
    fun whenThermometerConnected_onDisconnection_disconnectStatusShownInsteadOfStatus() {
        val mainScreenRobot = MainScreenRobot(context, composeRule)
        val fakeThermometerRepository = thermometerRepository as FakeThermometerRepository
        runBlocking<Unit> {
            mainScreenRobot
                .performClickOnConnectButton()
                .waitUntilCelsiusDegreesTextExists()
                .assertCelsiusDegreesTextVisibility(isDisplayed = true)

            fakeThermometerRepository.thermometerConnectionStatusesInternal.update {
                it.toMutableMap().apply {
                    put(scanResult.address, ThermometerConnectionStatus.DISCONNECTED)
                }
            }
            delay(200)

            mainScreenRobot
                .assertCelsiusDegreesTextVisibility(isDisplayed = false)
                .assertConnectThermometerBoxIsDisplayed()
                .assertDisconnectedTextIsDisplayed()
        }
    }

    @Test
    fun connectThermometer_whenConnectingThrowError_snackbarIsShown() {
        val mainScreenRobot = MainScreenRobot(context, composeRule)
        val fakeThermometerRepository = thermometerRepository as FakeThermometerRepository
        fakeThermometerRepository.isConnectToDeviceThrowingError = true

        runBlocking<Unit> {
            mainScreenRobot
                .performClickOnConnectButton()
                .waitUntilConnectingTextExists()
                .waitUntilSnackbarShown()
                .assertErrorDuringConnectingToDeviceIsDisplayed()
        }
    }
}
