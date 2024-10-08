package com.klewerro.mitemperature2alt.thermometerDetails.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.klewerro.mitemperature2alt.coreUi.LocalSpacing
import com.klewerro.mitemperature2alt.coreUi.theme.MiTemperature2AltTheme
import com.klewerro.mitemperature2alt.domain.model.HourlyRecord
import com.klewerro.mitemperature2alt.domain.model.SavedThermometer
import com.klewerro.mitemperature2alt.thermometerDetails.presentation.composable.DaySelector
import com.klewerro.mitemperature2alt.thermometerDetails.presentation.composable.HourlyRecordItem
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import com.klewerro.mitemperature2alt.coreUi.R as RCore

@Composable
fun ThermometerDetailsScreen(
    modifier: Modifier = Modifier,
    thermometerDetailsViewModel: ThermometerDetailsViewModel = hiltViewModel()
) {
    val spacing = LocalSpacing.current
    val thermometerDetailsState by thermometerDetailsViewModel.state.collectAsStateWithLifecycle()
    ThermometerDetailsScreenContent(
        state = thermometerDetailsState,
        onEvent = thermometerDetailsViewModel::onEvent,
        modifier = modifier
            .fillMaxSize()
            .padding(spacing.spaceScreen)
    )
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun ThermometerDetailsScreenContent(
    state: ThermometerDetailsState,
    onEvent: (ThermometerDetailsEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                utcTimeMillis <= System.currentTimeMillis()

            override fun isSelectableYear(year: Int): Boolean =
                year <= Clock.System.now().toLocalDateTime(TimeZone.UTC).year
        }
    )

    Column(modifier = modifier.fillMaxSize()) {
        state.thermometer?.let {
            FlowRow(
                horizontalArrangement = Arrangement.Center,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = it.name, style = MaterialTheme.typography.h1)
                Spacer(Modifier.width(spacing.spaceSmall))
                Text(
                    text = "(${it.address})",
                    style = MaterialTheme.typography.h1,
                    fontStyle = FontStyle.Italic
                )
            }
            Spacer(Modifier.height(spacing.spaceSmall))
            DaySelector(
                date = state.selectedDate,
                onPreviousDayClick = { onEvent(ThermometerDetailsEvent.OnPreviousDaySelected) },
                onNextDayClick = { onEvent(ThermometerDetailsEvent.OnNextDaySelected) },
                onDateClick = {
                    onEvent(ThermometerDetailsEvent.DatePickerOpened)
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(spacing.spaceSmall))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(spacing.spaceSmall),
                modifier = Modifier.weight(1f)
            ) {
                items(state.hourlyRecords) { hourlyRecord ->
                    HourlyRecordItem(hourlyRecord, modifier = Modifier.fillMaxWidth())
                    Divider()
                }
            }
        } ?: run {
            Text(
                text = stringResource(RCore.string.unexpected_error_occurred_try_again),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .weight(1f)
            )
        }

        if (state.isDatePickerOpened) {
            DatePickerDialog(
                onDismissRequest = {
                    onEvent(ThermometerDetailsEvent.DatePickerDismissed)
                },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let {
                            onEvent(
                                ThermometerDetailsEvent.DatePickerDateSelected(
                                    it
                                )
                            )
                        }
                    }) {
                        Text(stringResource(android.R.string.ok))
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        onEvent(ThermometerDetailsEvent.DatePickerDismissed)
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
    }
}

@Preview(showBackground = true)
@Composable
private fun ThermometerDetailsScreenPreview() {
    val state = ThermometerDetailsState(
        thermometer = SavedThermometer(
            "10:00:00:00:00",
            "Test name"
        ),
        hourlyRecords = listOf(
            HourlyRecord(
                0,
                LocalDateTime(2024, 10, 11, 12, 32),
                19.2f,
                22.6f,
                49,
                53
            ),
            HourlyRecord(
                0,
                LocalDateTime(2024, 10, 11, 12, 32),
                19.2f,
                22.6f,
                49,
                53
            ),
            HourlyRecord(
                0,
                LocalDateTime(2024, 10, 11, 12, 32),
                19.2f,
                22.6f,
                49,
                53
            )
        )
    )
    MiTemperature2AltTheme {
        ThermometerDetailsScreenContent(
            state = state,
            onEvent = {}
        )
    }
}
