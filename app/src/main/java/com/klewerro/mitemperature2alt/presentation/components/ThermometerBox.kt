package com.klewerro.mitemperature2alt.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.klewerro.mitemperature2alt.coreUi.LocalSpacing
import com.klewerro.mitemperature2alt.coreUi.theme.MiTemperature2AltTheme

@Composable
fun ThermometerBox(
    temperature: Float,
    humidity: Int,
    voltage: Float,
    modifier: Modifier = Modifier,
    nameComposable: @Composable () -> Unit
) {
    val spacing = LocalSpacing.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(268.dp)
            .shadow(
                spacing.elevationShadowNormal,
                shape = RoundedCornerShape(spacing.spaceNormal)
            )
            .clip(RoundedCornerShape(spacing.radiusNormal))
            .background(MaterialTheme.colors.background)
            .testTag("thermometerBox")
    ) {
        Box(
            modifier = Modifier
                .padding(18.dp)
                .clip(RoundedCornerShape(spacing.radiusNormal))
                .background(Color.LightGray)
        ) {
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

@Preview(showBackground = true)
@Composable
private fun ThermometerBoxPreview() {
    MiTemperature2AltTheme {
        ThermometerBox(
            temperature = 21.2f,
            51,
            1.23f
        ) {
            Text(text = "Example text composable")
        }
    }
}
