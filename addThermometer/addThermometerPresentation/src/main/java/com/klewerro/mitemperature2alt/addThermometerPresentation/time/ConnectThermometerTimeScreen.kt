package com.klewerro.mitemperature2alt.addThermometerPresentation.time

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.klewerro.mitemperature2alt.addThermometerPresentation.time.composable.TimePickerDialog
import com.klewerro.mitemperature2alt.addThermometerPresentation.time.composable.selectableCard.DeviceTimeSelectableCard
import com.klewerro.mitemperature2alt.addThermometerPresentation.time.composable.selectableCard.PickerTimeSelectableCard
import com.klewerro.mitemperature2alt.addThermometerPresentation.time.composable.selectableCard.ThermometerTimeSelectableCard
import com.klewerro.mitemperature2alt.core.util.LocalDateTimeUtils
import com.klewerro.mitemperature2alt.core.util.LocalDateTimeUtils.convertEpochMillisToLocalDate
import com.klewerro.mitemperature2alt.coreUi.LocalSpacing
import com.klewerro.mitemperature2alt.coreUi.theme.MiTemperature2AltTheme
import kotlinx.datetime.LocalTime

@Composable
fun ConnectThermometerTimeScreen(
    onTimeSend: () -> Unit,
    modifier: Modifier = Modifier,
    connectThermometerTimeViewModel: ConnectThermometerTimeViewModel = hiltViewModel()
) {
    val connectThermometerTimeState by
    connectThermometerTimeViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(true) {
        connectThermometerTimeViewModel.dataSend.collect {
            if (it) {
                onTimeSend()
            }
        }
    }

    ConnectThermometerTimeScreenContent(
        state = connectThermometerTimeState,
        onEvent = {
            connectThermometerTimeViewModel.onEvent(it)
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConnectThermometerTimeScreenContent(
    state: ConnectThermometerTimeState,
    onEvent: (ConnectThermometerTimeEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    val timePickerState = rememberTimePickerState(
        initialHour = state.deviceDateTime.hour,
        initialMinute = state.deviceDateTime.minute
    )
    val datePickerState = rememberDatePickerState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(spacing.spaceScreen),
        verticalArrangement = Arrangement.spacedBy(spacing.spaceNormal, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ThermometerTimeSelectableCard(
            state.thermometerDateTime,
            state.dateTimeType == DateTimeType.THERMOMETER,
            onClick = {
                onEvent(ConnectThermometerTimeEvent.SelectedOptionChanged(DateTimeType.THERMOMETER))
            }
        )

        DeviceTimeSelectableCard(
            state.deviceDateTime,
            state.dateTimeType == DateTimeType.DEVICE,
            onClick = {
                onEvent(ConnectThermometerTimeEvent.SelectedOptionChanged(DateTimeType.DEVICE))
            }
        )

        PickerTimeSelectableCard(
            state.userProvidedDateTime,
            state.dateTimeType == DateTimeType.USER_PROVIDED,
            onClick = {
                onEvent(
                    ConnectThermometerTimeEvent.SelectedOptionChanged(DateTimeType.USER_PROVIDED)
                )
            },
            onPickDateTimeClick = {
                onEvent(ConnectThermometerTimeEvent.OpenDatePicker)
            }
        )

        if (state.isDatePickerOpened) {
            DatePickerDialog(
                onDismissRequest = {
                    onEvent(ConnectThermometerTimeEvent.CloseDatePicker)
                },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let {
                            onEvent(
                                ConnectThermometerTimeEvent.DatePicked(
                                    it.convertEpochMillisToLocalDate()
                                )
                            )
                        }
                    }) {
                        Text(stringResource(android.R.string.ok))
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        onEvent(ConnectThermometerTimeEvent.CloseDatePicker)
                    }) {
                        Text(stringResource(android.R.string.cancel))
                    }
                }
            ) {
                DatePicker(
                    datePickerState,
                    showModeToggle = false
                )
            }
        }
        if (state.isTimePickerOpened) {
            TimePickerDialog(
                onDismiss = {
                    onEvent(ConnectThermometerTimeEvent.CloseTimePicker)
                },
                onConfirm = {
                    onEvent(
                        ConnectThermometerTimeEvent.TimePicked(
                            LocalTime(
                                timePickerState.hour,
                                timePickerState.minute
                            )
                        )
                    )
                }
            ) {
                TimePicker(
                    state = timePickerState
                )
            }
        }

        Button(
            onClick = {
                onEvent(ConnectThermometerTimeEvent.SendTimeToThermometer)
            },
            enabled = !state.sendingTime
        ) {
            Text("Send to the thermometer")
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
            null
        )
        ConnectThermometerTimeScreenContent(state = state, onEvent = {})
    }
}
