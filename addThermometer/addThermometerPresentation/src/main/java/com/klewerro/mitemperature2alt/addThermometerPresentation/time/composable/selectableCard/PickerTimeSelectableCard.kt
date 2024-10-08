package com.klewerro.mitemperature2alt.addThermometerPresentation.time.composable.selectableCard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.klewerro.mitemperature2alt.core.util.LocalDateTimeUtils
import com.klewerro.mitemperature2alt.core.util.LocalDateTimeUtils.formatToFullHourDate
import com.klewerro.mitemperature2alt.coreUi.LocalSpacing
import com.klewerro.mitemperature2alt.coreUi.theme.MiTemperature2AltTheme
import kotlinx.datetime.LocalDateTime

@Composable
fun PickerTimeSelectableCard(
    dateTime: LocalDateTime?,
    isSelected: Boolean,
    onClick: () -> Unit,
    onPickDateTimeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    SelectableCard(
        isSelected = isSelected,
        onClick = onClick,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(spacing.spaceSmall),
            verticalArrangement = Arrangement.spacedBy(spacing.spaceSmall)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                dateTime?.let {
                    Text(
                        text = dateTime.formatToFullHourDate(shortenedYear = false),
                        style = MaterialTheme.typography.h3,
                        modifier = Modifier.alignByBaseline()
                    )
                } ?: run {
                    // Only for move text to the end.
                    PickDateTimeButton(onPickDateTimeClick, modifier = Modifier.alignByBaseline())
                }

                Text(
                    text = "User time",
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.alignByBaseline()
                )
            }
            Text(
                text = "Time selected using time picker.",
                style = MaterialTheme.typography.body1
            )
            if (dateTime != null) {
                PickDateTimeButton(
                    onPickDateTimeClick,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

// @Composable
// private fun PickDateTimeButtons(
//    onPickDateClick: () -> Unit,
//    onPickTimeClick: () -> Unit,
//    modifier: Modifier = Modifier
// ) {
//    Row {
//        PickDateButton(
//            onPickDateClick
//        )
//        PickTimeButton(
//            onPickTimeClick
//        )
//    }
// }

@Composable
private fun PickDateTimeButton(onButtonClick: () -> Unit, modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current
    OutlinedButton(
        onClick = onButtonClick,
        modifier = modifier
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Pick date and time")
            Spacer(modifier = Modifier.width(spacing.spaceExtraSmall))
            Icon(Icons.Default.AccessTime, null)
        }
    }
}

// @Composable
// private fun PickDateButton(onButtonClick: () -> Unit, modifier: Modifier = Modifier) {
//    val spacing = LocalSpacing.current
//    OutlinedButton(
//        onClick = onButtonClick,
//        modifier = modifier
//    ) {
//        Row(verticalAlignment = Alignment.CenterVertically) {
//            Text("Pick date")
//            Spacer(modifier = Modifier.width(spacing.spaceExtraSmall))
//            Icon(Icons.Default.CalendarMonth, null)
//        }
//    }
// }

@Preview
@Composable
private fun PickerTimeSelectableCardPreview() {
    MiTemperature2AltTheme {
        PickerTimeSelectableCard(
            dateTime = LocalDateTimeUtils.getCurrentUtcTime(),
            isSelected = false,
            onClick = {},
            onPickDateTimeClick = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview
@Composable
private fun PickerTimeSelectableCardPreviewTimeNull() {
    MiTemperature2AltTheme {
        PickerTimeSelectableCard(
            dateTime = null,
            isSelected = false,
            onClick = {},
            onPickDateTimeClick = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview
@Composable
private fun PickerTimeSelectableCardPreviewSelected() {
    MiTemperature2AltTheme {
        PickerTimeSelectableCard(
            dateTime = LocalDateTimeUtils.getCurrentUtcTime(),
            isSelected = true,
            onClick = {},
            onPickDateTimeClick = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}
