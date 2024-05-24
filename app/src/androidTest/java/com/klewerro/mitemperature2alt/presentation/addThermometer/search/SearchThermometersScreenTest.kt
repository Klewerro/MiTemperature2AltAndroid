package com.klewerro.mitemperature2alt.presentation.addThermometer.search

import android.Manifest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.rule.GrantPermissionRule
import com.klewerro.mitemperature2alt.MainActivity
import com.klewerro.mitemperature2alt.MiTemperature2AltAndroidTest
import com.klewerro.mitemperature2alt.coreTest.generators.ThermometerScanResultsGenerator
import com.klewerro.mitemperature2alt.coreUi.R
import com.klewerro.mitemperature2alt.coreUi.util.isAndroid12OrGreater
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalTestApi::class)
@HiltAndroidTest
class SearchThermometersScreenTest : MiTemperature2AltAndroidTest() {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val grantBlePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        *if (isAndroid12OrGreater()) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT
            )
        } else {
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    )

    @Test
    fun afterTopBarPlusIconClick_navigateToSearchThermometersScreen() {
        composeRule
            .onNodeWithContentDescription(context.getString(R.string.add_new_thermometer))
            .performClick()
        composeRule
            .onNodeWithText(context.resources.getString(R.string.scan_for_devices))
            .assertIsDisplayed()
    }

    @Test
    fun afterThermometerListItemClick_whenThermometerNameEnteredByUser_SavedThermometerIsVisibleOnMainScreen() =
        runBlocking<Unit> {
            val address = ThermometerScanResultsGenerator.scanResult1.address
            val thermometerName = "new thermometer name"

            SearchThermometersRobot(context, composeRule)
                .navigateToAddNewThermometerScreen()
                .waitUntilFirstThermometerAppearAndPerformClickOnIt()
                .assertThermometerProgressImageIsDisplayed()
                .assertConnectingTextIsDisplayed(address)
                .waitUntilThermometerConnectedCheckIsDisplayed()
                .assertConnectedToThermometerTextIsDisplayed(address)
                .waitUntilThermometerNameScreenAppear()
                .assertThermometerBoxIsDisplayed()
                .performThermometerNameInput(thermometerName)
                .performClickOnSaveThermometerButton()
                .assertMainScreenIsDisplayed()
                .assertThermometerBoxWithThermometerNameIsDisplayedOnMainScreen(thermometerName)
        }

    @Test
    fun afterThermometerListItemClick_whenThermometerNameIsNotEntered_presentErrorAfterSaveClick_andAfterEntering1LetterHideItAndSaveThermometer() =
        runBlocking<Unit> {
            val address = ThermometerScanResultsGenerator.scanResult1.address
            val oneLetterName = "a"

            SearchThermometersRobot(context, composeRule)
                .navigateToAddNewThermometerScreen()
                .waitUntilFirstThermometerAppearAndPerformClickOnIt()
                .assertThermometerProgressImageIsDisplayed()
                .assertConnectingTextIsDisplayed(address)
                .waitUntilThermometerConnectedCheckIsDisplayed()
                .assertConnectedToThermometerTextIsDisplayed(address)
                .waitUntilThermometerNameScreenAppear()
                .assertThermometerBoxIsDisplayed()
                .performClickOnSaveThermometerButton()
                .assertErrorTextVisibility(isDisplayed = true)
                .performThermometerNameInput(oneLetterName)
                .assertErrorTextVisibility(isDisplayed = false)
                .performClickOnSaveThermometerButton()
                .assertMainScreenIsDisplayed()
                .assertThermometerBoxWithThermometerNameIsDisplayedOnMainScreen(oneLetterName)
        }
}
