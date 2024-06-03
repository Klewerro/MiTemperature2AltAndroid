package com.klewerro.mitemperature2alt.presentation.mainscreen

import android.content.Context
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasParent
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.klewerro.mitemperature2alt.MainActivityComposeRule
import com.klewerro.mitemperature2alt.coreUi.R

class MainScreenRobot(
    private val context: Context,
    private val composeRule: MainActivityComposeRule
) {
    private val connectThermometerBoxNode = composeRule
        .onNode(
            hasClickAction() and
                hasText(context.getString(R.string.connect)) and
                hasParent(hasTestTag("thermometerBox"))
        )

    private val hasCelsiusTextMatcher = hasText("°C", substring = true, ignoreCase = true)

    fun performClickOnConnectButton() = apply {
        connectThermometerBoxNode.performClick()
    }

    @OptIn(ExperimentalTestApi::class)
    fun waitUntilConnectingTextExists() = apply {
        composeRule
            .waitUntilExactlyOneExists(
                hasText(context.getString(R.string.connecting), ignoreCase = true),
                1_500
            )
    }

    @OptIn(ExperimentalTestApi::class)
    fun waitUntilCelsiusDegreesTextExists() = apply {
        composeRule.waitUntilExactlyOneExists(
            hasCelsiusTextMatcher,
            2_500
        )
    }

    @OptIn(ExperimentalTestApi::class)
    fun waitUntilSnackbarShown() = apply {
        composeRule
            .waitUntilAtLeastOneExists(
                hasText(
                    context.getString(R.string.unexpected_error_during_connecting_to_device)
                ),
                timeoutMillis = 2_500
            )
    }

    fun assertConnectingTextIsDisplayed() = apply {
        composeRule
            .onNodeWithText(context.getString(R.string.connecting), ignoreCase = true)
            .assertIsDisplayed()
    }

    fun assertDisconnectedTextIsDisplayed() = apply {
        composeRule
            .onNodeWithText(context.getString(R.string.disconnected), ignoreCase = true)
            .assertIsDisplayed()
    }

    fun assertProgressBarVisibility(isDisplayed: Boolean) = apply {
        composeRule
            .onNode(
                SemanticsMatcher.keyIsDefined(SemanticsProperties.ProgressBarRangeInfo)
            )
            .run {
                if (isDisplayed) {
                    assertIsDisplayed()
                } else {
                    assertIsNotDisplayed()
                }
            }
    }

    fun assertCelsiusDegreesTextVisibility(isDisplayed: Boolean) = apply {
        composeRule
            .onNode(hasCelsiusTextMatcher)
            .run {
                if (isDisplayed) {
                    assertIsDisplayed()
                } else {
                    assertIsNotDisplayed()
                }
            }
    }

    fun assertConnectThermometerBoxIsDisplayed() = apply {
        connectThermometerBoxNode
            .assertIsDisplayed()
    }

    fun assertErrorDuringConnectingToDeviceIsDisplayed() = apply {
        composeRule
            .onNodeWithText(
                context.getString(R.string.unexpected_error_during_connecting_to_device),
                ignoreCase = true,
                useUnmergedTree = true
            )
            .assertIsDisplayed()
    }
}
