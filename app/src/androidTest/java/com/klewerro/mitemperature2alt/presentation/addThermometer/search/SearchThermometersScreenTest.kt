package com.klewerro.mitemperature2alt.presentation.addThermometer.search

import android.Manifest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasParent
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.rule.GrantPermissionRule
import com.klewerro.mitemperature2alt.MainActivity
import com.klewerro.mitemperature2alt.MiTemperature2AltAndroidTest
import com.klewerro.mitemperature2alt.R
import com.klewerro.mitemperature2alt.coreTest.generators.ThermometerScanResultsGenerator
import com.klewerro.mitemperature2alt.presentation.util.isAndroid12OrGreater
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

            composeRule
                .onNodeWithContentDescription(context.getString(R.string.add_new_thermometer))
                .performClick()

            composeRule.waitUntilAtLeastOneExists(
                hasClickAction() and hasParent(hasScrollAction()),
                10_000
            )

            composeRule
                .onNodeWithText(address)
                .performClick()

            composeRule
                .onNodeWithContentDescription(context.resources.getString(R.string.humidity_sensor))
                .assertIsDisplayed()
            composeRule.onNodeWithText(
                context.resources.getString(R.string.connecting_to_ADDRESS_thermometer, address)
            ).assertIsDisplayed()

            composeRule.waitUntilAtLeastOneExists(
                hasContentDescription(context.resources.getString(R.string.thermometer_connected)),
                10_000
            )
            composeRule.onNodeWithText(
                context.resources.getString(R.string.connected_to_ADDRESS_thermometer, address)
            ).assertIsDisplayed()

            composeRule.waitUntilAtLeastOneExists(
                hasClickAction() and hasText(context.resources.getString(R.string.save)),
                1_500
            )

            composeRule
                .onNodeWithText(context.resources.getString(R.string.thermometer_name))
                .assertIsDisplayed()
            composeRule
                .onNodeWithTag("thermometerBox")
                .assertIsDisplayed()

            // Entering text
            val thermometerName = "new thermometer name"
            composeRule
                .onNodeWithText(context.resources.getString(R.string.thermometer_name))
                .performTextInput(thermometerName)

            composeRule
                .onNodeWithText(context.resources.getString(R.string.save))
                .performClick()

            composeRule
                .onNodeWithText(context.resources.getString(R.string.title_mi_temperature_2_alt))
                .assertIsDisplayed()

            composeRule
                .onNodeWithText(thermometerName)
                .assertIsDisplayed()
        }

    @Test
    fun afterThermometerListItemClick_whenThermometerNameIsNotEntered_presentErrorAfterSaveClick_andAfterEntering1LetterHideItAndSaveThermometer() =
        runBlocking<Unit> {
            val address = ThermometerScanResultsGenerator.scanResult1.address

            composeRule
                .onNodeWithContentDescription(context.getString(R.string.add_new_thermometer))
                .performClick()

            composeRule.waitUntilAtLeastOneExists(
                hasClickAction() and hasParent(hasScrollAction()),
                10_000
            )

            composeRule
                .onNodeWithText(address)
                .performClick()

            composeRule
                .onNodeWithContentDescription(context.resources.getString(R.string.humidity_sensor))
                .assertIsDisplayed()
            composeRule.onNodeWithText(
                context.resources.getString(R.string.connecting_to_ADDRESS_thermometer, address)
            ).assertIsDisplayed()

            composeRule.waitUntilAtLeastOneExists(
                hasContentDescription(context.resources.getString(R.string.thermometer_connected)),
                10_000
            )
            composeRule.onNodeWithText(
                context.resources.getString(R.string.connected_to_ADDRESS_thermometer, address)
            ).assertIsDisplayed()

            composeRule.waitUntilAtLeastOneExists(
                hasClickAction() and hasText(context.resources.getString(R.string.save)),
                1_500
            )

            composeRule
                .onNodeWithText(context.resources.getString(R.string.thermometer_name))
                .assertIsDisplayed()
            composeRule
                .onNodeWithTag("thermometerBox")
                .assertIsDisplayed()

            // Saving thermometer with empty name
            composeRule
                .onNodeWithText(context.resources.getString(R.string.save))
                .performClick()

            // Check if error is presented on the screen
            composeRule
                .onNodeWithText(
                    context.resources.getString(R.string.thermometer_name_must_not_be_empty)
                )
                .assertIsDisplayed()

            composeRule
                .onNodeWithText(context.resources.getString(R.string.thermometer_name))
                .performTextInput("a")

            // Check if error is hidden when 1 letter entered
            composeRule
                .onNodeWithText(
                    context.resources.getString(R.string.thermometer_name_must_not_be_empty)
                )
                .assertIsNotDisplayed()

            // Saving thermometer with 1 letter name
            composeRule
                .onNodeWithText(context.resources.getString(R.string.save))
                .performClick()

            // Test if correct name is saved and displayed
            composeRule
                .onNodeWithText(context.resources.getString(R.string.title_mi_temperature_2_alt))
                .assertIsDisplayed()

            composeRule
                .onNodeWithText("a")
                .assertIsDisplayed()
        }
}
