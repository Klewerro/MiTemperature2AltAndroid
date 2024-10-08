package com.klewerro.mitemperature2alt.addThermometerPresentation.time.composable.selectableCard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.klewerro.mitemperature2alt.core.util.LocalDateTimeUtils
import com.klewerro.mitemperature2alt.core.util.LocalDateTimeUtils.formatToFullHourDate
import com.klewerro.mitemperature2alt.coreUi.LocalSpacing
import com.klewerro.mitemperature2alt.coreUi.theme.MiTemperature2AltTheme
import kotlinx.datetime.LocalDateTime

@Composable
fun ThermometerTimeSelectableCard(
    dateTime: LocalDateTime?,
    isSelected: Boolean,
    onClick: () -> Unit,
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
                        text = it.formatToFullHourDate(shortenedYear = false),
                        style = MaterialTheme.typography.h3,
                        modifier = Modifier.alignByBaseline()
                    )
                } ?: run {
                    CircularProgressIndicator()
                }

                Text(
                    text = "Thermometer time",
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.alignByBaseline()
                )
            }
            Text(
                text = "Time stored in thermometer device.",
                style = MaterialTheme.typography.body1
            )
        }
    }
}

@Preview
@Composable
private fun ThermometerTimeSelectableCardPreview() {
    MiTemperature2AltTheme {
        ThermometerTimeSelectableCard(
            dateTime = LocalDateTimeUtils.getCurrentUtcTime(),
            isSelected = false,
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview
@Composable
private fun ThermometerTimeSelectableCardPreviewDateTimeNull() {
    MiTemperature2AltTheme {
        ThermometerTimeSelectableCard(
            dateTime = null,
            isSelected = false,
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview
@Composable
private fun ThermometerTimeSelectableCardPreviewSelected() {
    MiTemperature2AltTheme {
        ThermometerTimeSelectableCard(
            dateTime = LocalDateTimeUtils.getCurrentUtcTime(),
            isSelected = true,
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}
