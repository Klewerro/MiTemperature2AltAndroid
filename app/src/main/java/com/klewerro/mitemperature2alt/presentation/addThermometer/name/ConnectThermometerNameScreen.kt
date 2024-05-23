package com.klewerro.mitemperature2alt.presentation.addThermometer.name

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.klewerro.mitemperature2alt.coreUi.LocalSpacing
import com.klewerro.mitemperature2alt.coreUi.R
import com.klewerro.mitemperature2alt.coreUi.theme.MiTemperature2AltTheme
import com.klewerro.mitemperature2alt.domain.model.ThermometerStatus
import com.klewerro.mitemperature2alt.presentation.addThermometer.ConnectThermometerEvent
import com.klewerro.mitemperature2alt.presentation.addThermometer.ConnectThermometerState
import com.klewerro.mitemperature2alt.presentation.addThermometer.ConnectThermometerViewModel
import com.klewerro.mitemperature2alt.presentation.util.UiText
import com.klewerro.mitemperature2alt.presentation.util.clearFocusOnClick

@Composable
fun ConnectThermometerNameScreen(
    viewModel: ConnectThermometerViewModel,
    onThermometerSaved: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = state.thermometerSaved) {
        if (state.thermometerSaved) {
            onThermometerSaved()
        }
    }

    ConnectThermometerNameScreenContent(
        state = state,
        onNameChanged = {
            viewModel.onEvent(ConnectThermometerEvent.ChangeThermometerName(it))
        },
        onSaveClick = {
            viewModel.onEvent(ConnectThermometerEvent.SaveThermometer)
        },
        modifier = modifier
    )
}

@Composable
private fun ConnectThermometerNameScreenContent(
    state: ConnectThermometerState,
    onNameChanged: (String) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val spacing = LocalSpacing.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    Column(
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .clearFocusOnClick(focusManager)
    ) {
        EnterNameThermometerBox(
            temperature = state.connectThermometerStatus?.temperature ?: 0.0f,
            humidity = state.connectThermometerStatus?.humidity ?: 0,
            voltage = 0.00f,
            text = state.thermometerName,
            onTextChange = onNameChanged,
            isError = state.error != null,
            onDone = {
                keyboardController?.hide()
                onSaveClick()
            },
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .focusRequester(focusRequester)
        )

        AnimatedVisibility(visible = state.error != null) {
            Text(
                text = state.error?.asString(context) ?: stringResource(
                    id = R.string.unexpected_error_occurred_try_again
                ),
                color = MaterialTheme.colors.error
            )
        }

        Button(
            onClick = onSaveClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.spaceExtraLarge)
        ) {
            Text(
                text = stringResource(id = R.string.save),
                style = MaterialTheme.typography.h3,
                modifier = Modifier.padding(spacing.spaceSmall)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ConnectThermometerNameScreenPreview() {
    MiTemperature2AltTheme {
        val state = ConnectThermometerState(
            thermometerAddress = "00:00:00:00",
            connectThermometerStatus = ThermometerStatus(
                21.5f,
                51,
                1.23f
            )
        )
        ConnectThermometerNameScreenContent(
            state = state,
            onNameChanged = {},
            {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ConnectThermometerNameScreenErrorPreview() {
    MiTemperature2AltTheme {
        val state = ConnectThermometerState(
            thermometerAddress = "00:00:00:00",
            connectThermometerStatus = ThermometerStatus(
                21.5f,
                51,
                1.23f
            ),
            error = UiText.DynamicString("Error message")
        )
        ConnectThermometerNameScreenContent(
            state = state,
            onNameChanged = {},
            {}
        )
    }
}
