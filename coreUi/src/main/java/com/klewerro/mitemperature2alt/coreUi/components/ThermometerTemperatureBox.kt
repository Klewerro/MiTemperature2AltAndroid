package com.klewerro.mitemperature2alt.coreUi.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.sp
import com.klewerro.mitemperature2alt.coreUi.theme.MiTemperature2AltTheme

@Composable
fun ThermometerTemperatureBox(
    temperature: Float,
    humidity: Int,
    voltage: Float,
    modifier: Modifier = Modifier,
    nameComposable: @Composable () -> Unit
) {
    ThermometerBox(modifier) {
        ThermometerContent(
            temperature = temperature,
            humidity = humidity,
            voltage = voltage,
            modifier = Modifier.fillMaxSize()
        ) {
            nameComposable()
        }
    }
}

@Composable
private fun ThermometerContent(
    temperature: Float,
    humidity: Int,
    voltage: Float,
    modifier: Modifier = Modifier,
    nameComposable: @Composable () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        nameComposable()
        Text(
            text = "$temperature°C",
            fontSize = 56.sp // Todo: Create typography, with clock-like font and it's heights
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = "$humidity%",
                fontSize = 38.sp
            )
            Text(
                text = "${voltage}V",
                fontSize = 38.sp
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun ThermometerBoxPreview() {
    MiTemperature2AltTheme {
        ThermometerTemperatureBox(
            temperature = 21.2f,
            51,
            1.23f
        ) {
            Text(text = "Example text composable")
        }
    }
}
