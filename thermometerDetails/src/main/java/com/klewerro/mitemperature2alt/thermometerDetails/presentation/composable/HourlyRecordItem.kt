package com.klewerro.mitemperature2alt.thermometerDetails.presentation.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.klewerro.mitemperature2alt.core.util.LocalDateTimeUtils.formatTest
import com.klewerro.mitemperature2alt.coreUi.LocalSpacing
import com.klewerro.mitemperature2alt.coreUi.theme.MiTemperature2AltTheme
import com.klewerro.mitemperature2alt.domain.model.HourlyRecord
import kotlinx.datetime.LocalDateTime
import com.klewerro.mitemperature2alt.coreUi.R as RCore

@Composable
fun HourlyRecordItem(hourlyRecord: HourlyRecord, modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.Thermostat,
            contentDescription = null,
            tint = MaterialTheme.colors.primaryVariant,
            modifier = Modifier.size(32.dp)
        )
        Column {
            IconTextItem(
                imageVector = Icons.Default.KeyboardArrowUp,
                imageTint = Color.Red,
                text = hourlyRecord.temperatureMax.toString(),
                contentDescription = stringResource(RCore.string.max_temperature)
            )
            IconTextItem(
                imageVector = Icons.Default.KeyboardArrowDown,
                imageTint = Color.Blue,
                text = hourlyRecord.temperatureMin.toString(),
                contentDescription = stringResource(RCore.string.min_temperature)
            )
        }
        Spacer(modifier = Modifier.width(spacing.spaceNormal))
        Icon(
            imageVector = Icons.Default.WaterDrop,
            contentDescription = null,
            tint = MaterialTheme.colors.primaryVariant,
            modifier = Modifier.size(32.dp)
        )
        Column {
            IconTextItem(
                imageVector = Icons.Default.KeyboardArrowUp,
                imageTint = Color.Red,
                text = hourlyRecord.humidityMax.toString() + "%",
                contentDescription = stringResource(RCore.string.max_humidity)
            )
            IconTextItem(
                imageVector = Icons.Default.KeyboardArrowDown,
                imageTint = Color.Blue,
                text = hourlyRecord.humidityMin.toString() + "%",
                contentDescription = stringResource(RCore.string.min_humidity)
            )
        }

        Spacer(modifier = Modifier.width(spacing.spaceNormal))
        Text(
            text = hourlyRecord.dateTime.formatTest(),
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun IconTextItem(
    imageVector: ImageVector,
    imageTint: Color,
    text: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.h3
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.height(IntrinsicSize.Max)
    ) {
        Text(
            text = text,
            style = textStyle
        )
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = imageTint,
            modifier = Modifier.fillMaxHeight()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HourlyRecordItemPreview() {
    MiTemperature2AltTheme {
        HourlyRecordItem(
            hourlyRecord = HourlyRecord(
                0,
                LocalDateTime(2024, 10, 11, 12, 32),
                19.2f,
                22.6f,
                49,
                53
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
