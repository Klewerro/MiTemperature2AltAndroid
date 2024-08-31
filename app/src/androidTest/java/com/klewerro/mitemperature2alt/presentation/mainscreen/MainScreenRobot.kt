package com.klewerro.mitemperature2alt.presentation.mainscreen

import android.content.Context
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.klewerro.mitemperature2alt.MainActivityComposeRule
import com.klewerro.mitemperature2alt.ProjectSemanticMatchers
import com.klewerro.mitemperature2alt.coreUi.R

class MainScreenRobot(
    private val context: Context,
    private val composeRule: MainActivityComposeRule
) {
    private val connectThermometerBoxNode = composeRule
        .onNode(
            hasClickAction() and
                hasText(context.getString(R.string.connect)) and
                ProjectSemanticMatchers.thermometerBox
        )

    private val hasCelsiusTextMatcher = hasText("°C", substring = true, ignoreCase = true)

    fun performClickOnConnectButton() = apply {
        connectThermometerBoxNode.performClick()
    }

    val test: SemanticsMatcher = hasText("", ignoreCase = true)

    @OptIn(ExperimentalTestApi::class)
    fun waitUntilConnectingTextExists() = apply {
        composeRule
            .waitUntilExactlyOneExists(
                hasText(context.getString(R.string.connecting), ignoreCase = true) and
                    ProjectSemanticMatchers.thermometerBox,
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
//        composeRule
//            .onNodeWithText(context.getString(R.string.connecting), ignoreCase = true)
//            .assertIsDisplayed()
        composeRule
            .onNode(
                hasText(context.getString(R.string.connecting), ignoreCase = true) and
                    ProjectSemanticMatchers.thermometerBox
            )
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
                SemanticsMatcher.keyIsDefined(SemanticsProperties.ProgressBarRangeInfo) and
                    ProjectSemanticMatchers.thermometerBox
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
