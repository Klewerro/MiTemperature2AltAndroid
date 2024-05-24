package com.klewerro.mitemperature2alt.presentation.addThermometer.search

import android.content.Context
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasParent
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.klewerro.mitemperature2alt.MainActivityComposeRule
import com.klewerro.mitemperature2alt.R

@OptIn(ExperimentalTestApi::class)
class SearchThermometersRobot(
    private val context: Context,
    private val composeRule: MainActivityComposeRule
) {
    fun navigateToAddNewThermometerScreen() = apply {
        composeRule
            .onNodeWithContentDescription(context.getString(R.string.add_new_thermometer))
            .performClick()
    }

    fun waitUntilFirstThermometerAppearAndPerformClickOnIt() = apply {
        composeRule.waitUntilAtLeastOneExists(
            hasClickAction() and hasParent(hasScrollAction()),
            10_000
        )

        composeRule
            .onNode(hasClickAction() and hasParent(hasScrollAction()))
            .performClick()
    }

    fun assertThermometerProgressImageIsDisplayed() = apply {
        composeRule
            .onNodeWithContentDescription(context.resources.getString(R.string.humidity_sensor))
            .assertIsDisplayed()
    }

    fun assertConnectingTextIsDisplayed(address: String) = apply {
        composeRule.onNodeWithText(
            context.resources.getString(R.string.connecting_to_ADDRESS_thermometer, address)
        ).assertIsDisplayed()
    }

    fun waitUntilThermometerConnectedCheckIsDisplayed() = apply {
        composeRule.waitUntilAtLeastOneExists(
            hasContentDescription(context.resources.getString(R.string.thermometer_connected)),
            10_000
        )
    }

    fun assertConnectedToThermometerTextIsDisplayed(address: String) = apply {
        composeRule.onNodeWithText(
            context.resources.getString(R.string.connected_to_ADDRESS_thermometer, address)
        ).assertIsDisplayed()
    }

    fun waitUntilThermometerNameScreenAppear() = apply {
        composeRule.waitUntilAtLeastOneExists(
            hasClickAction() and hasText(context.resources.getString(R.string.save)),
            1_500 // Delay after check icon
        )
    }

    fun assertThermometerBoxIsDisplayed() = apply {
        composeRule
            .onNodeWithText(context.resources.getString(R.string.thermometer_name))
            .assertIsDisplayed()
        composeRule
            .onNodeWithTag("thermometerBox")
            .assertIsDisplayed()
    }

    fun assertErrorTextVisibility(isDisplayed: Boolean) = apply {
        composeRule
            .onNodeWithText(
                context.resources.getString(R.string.thermometer_name_must_not_be_empty)
            ).run {
                if (isDisplayed) {
                    assertIsDisplayed()
                } else {
                    assertIsNotDisplayed()
                }
            }
    }

    fun performThermometerNameInput(thermometerName: String) = apply {
        composeRule
            .onNodeWithText(context.resources.getString(R.string.thermometer_name))
            .performTextInput(thermometerName)
    }

    fun performClickOnSaveThermometerButton() = apply {
        composeRule
            .onNodeWithText(context.resources.getString(R.string.save))
            .performClick()
    }

    fun assertMainScreenIsDisplayed() = apply {
        composeRule
            .onNodeWithText(context.resources.getString(R.string.title_mi_temperature_2_alt))
            .assertIsDisplayed()
    }

    fun assertThermometerBoxWithThermometerNameIsDisplayedOnMainScreen(thermometerName: String) =
        apply {
            composeRule
                .onNodeWithText(thermometerName)
                .assertIsDisplayed()
        }
}
