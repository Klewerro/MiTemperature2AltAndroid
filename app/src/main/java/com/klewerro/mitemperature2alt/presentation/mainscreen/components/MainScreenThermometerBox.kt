package com.klewerro.mitemperature2alt.presentation.mainscreen.components

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.klewerro.mitemperature2alt.coreUi.components.ThermometerBox
import com.klewerro.mitemperature2alt.coreUi.theme.MiTemperature2AltTheme
import com.klewerro.mitemperature2alt.domain.model.ThermometerDevice

@Composable
fun MainScreenThermometerBox(
    thermometerDevice: ThermometerDevice,
    onRefreshClick: () -> Unit,
    onSubscribeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(268.dp)
    ) {
        ThermometerBox(
            thermometerDevice.temperature,
            thermometerDevice.humidity,
            thermometerDevice.voltage,
            modifier.fillMaxSize()
        ) {
            Text(text = thermometerDevice.name)
        }
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
    }
}

@Preview(showBackground = true)
@Preview(name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun MainScreenThermometerBoxPreview() {
    MiTemperature2AltTheme {
        MainScreenThermometerBox(
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
}
