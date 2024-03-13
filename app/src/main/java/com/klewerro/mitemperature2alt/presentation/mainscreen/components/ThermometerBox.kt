package com.klewerro.mitemperature2alt.presentation.mainscreen.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.klewerro.mitemperature2alt.domain.model.ThermometerDevice
import com.klewerro.mitemperature2alt.ui.LocalSpacing

@Composable
fun ThermometerBox(
    thermometerDevice: ThermometerDevice,
    onRefreshClick: () -> Unit,
    onSubscribeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
            .shadow(
                spacing.elevationShadowNormal,
                shape = RoundedCornerShape(spacing.spaceNormal)
            )
            .clip(RoundedCornerShape(spacing.radiusNormal))
            .background(MaterialTheme.colors.background)
    ) {
        Icon(
            Icons.Default.Refresh,
            "Refresh thermometer temperature",
            tint = MaterialTheme.colors.onBackground,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .clickable { onRefreshClick() }
                .padding(2.dp)
        )
        Icon(
            Icons.Default.Notifications,
            "Refresh thermometer temperature",
            tint = MaterialTheme.colors.onBackground,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .clickable { onSubscribeClick() }
                .padding(2.dp)
        )
        Box(
            modifier = Modifier
                .padding(18.dp)
                .clip(RoundedCornerShape(spacing.radiusNormal))
                .background(Color.LightGray)
        ) {
            ThermometerValuesColumn(
                name = thermometerDevice.name,
                temperature = thermometerDevice.temperature,
                humidity = thermometerDevice.humidity,
                voltage = thermometerDevice.voltage,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun ThermometerValuesColumn(
    name: String,
    temperature: Float,
    humidity: Int,
    voltage: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(text = name)
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
@Preview(name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ThermometerBoxPreview() {
    ThermometerBox(
        ThermometerDevice(
            "address",
            "name",
            temperature = 21.1f,
            humidity = 56,
            voltage = 1.231f,
            -1
        ),
        onRefreshClick = {},
        onSubscribeClick = {}
    )
}
