package com.klewerro.mitemperature2alt.thermometerDetails.presentation.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.klewerro.mitemperature2alt.core.util.LocalDateTimeUtils.formatToSimpleDate
import com.klewerro.mitemperature2alt.coreUi.R
import com.klewerro.mitemperature2alt.coreUi.theme.MiTemperature2AltTheme
import kotlinx.datetime.LocalDate

@Composable
fun DaySelector(
    date: LocalDate,
    onPreviousDayClick: () -> Unit,
    onNextDayClick: () -> Unit,
    onDateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousDayClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowBackIos,
                contentDescription = stringResource(id = R.string.previous_day),
                tint = MaterialTheme.colors.primary
            )
        }
        Text(
            text = date.formatToSimpleDate(shortenedYear = false),
            style = MaterialTheme.typography.h2,
            modifier = Modifier.clickable {
                onDateClick()
            }
        )
        Row {
            IconButton(onClick = onNextDayClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowForwardIos,
                    contentDescription = stringResource(id = R.string.next_day),
                    tint = MaterialTheme.colors.primary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DaySelectorPreview() {
    MiTemperature2AltTheme {
        DaySelector(
            date = LocalDate(year = 2024, monthNumber = 10, dayOfMonth = 29),
            onPreviousDayClick = {},
            onNextDayClick = {},
            onDateClick = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}
