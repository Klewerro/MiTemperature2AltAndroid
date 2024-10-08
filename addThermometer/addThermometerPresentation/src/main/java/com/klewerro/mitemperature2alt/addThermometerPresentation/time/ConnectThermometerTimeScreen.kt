package com.klewerro.mitemperature2alt.addThermometerPresentation.time

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.klewerro.mitemperature2alt.core.util.LocalDateTimeUtils
import com.klewerro.mitemperature2alt.core.util.LocalDateTimeUtils.formatToFullHourDate
import com.klewerro.mitemperature2alt.coreUi.theme.MiTemperature2AltTheme

@Composable
fun ConnectThermometerTimeScreen(
    onNextButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    connectThermometerTimeViewModel: ConnectThermometerTimeViewModel = hiltViewModel()
) {
    val connectThermometerTimeState by
    connectThermometerTimeViewModel.state.collectAsStateWithLifecycle()
    ConnectThermometerTimeScreenContent(
        state = connectThermometerTimeState,
        onRadioButtonClick = { radioIndex ->
            connectThermometerTimeViewModel.selectOption(radioIndex)
        },
        onNextButtonClick = onNextButtonClick,
        modifier = modifier
    )
}

@Composable
private fun ConnectThermometerTimeScreenContent(
    state: ConnectThermometerTimeState,
    onRadioButtonClick: (Int) -> Unit,
    onNextButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = state.selectedOption == 0,
                onClick = {
                    onRadioButtonClick(0)
                }
            )
            state.thermometerTime?.let {
                Text(text = it.formatToFullHourDate(shortenedYear = false))
            } ?: run {
                CircularProgressIndicator()
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = state.selectedOption == 1,
                onClick = {
                    onRadioButtonClick(1)
                }
            )
            Text(text = state.deviceTime.formatToFullHourDate(shortenedYear = false))
        }
        // Todo: Add custom time option
        Button(onClick = onNextButtonClick) {
            Text("Next")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ConnectThermometerTimeScreenContentPreview() {
    MiTemperature2AltTheme {
        val state = ConnectThermometerTimeState(
            LocalDateTimeUtils.getCurrentUtcTime(),
            LocalDateTimeUtils.getCurrentUtcTime(),
            0
        )
        ConnectThermometerTimeScreenContent(state = state, {}, {})
    }
}
