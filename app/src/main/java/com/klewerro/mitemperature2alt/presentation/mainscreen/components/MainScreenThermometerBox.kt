package com.klewerro.mitemperature2alt.presentation.mainscreen.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.klewerro.mitemperature2alt.coreUi.LocalSpacing
import com.klewerro.mitemperature2alt.coreUi.R
import com.klewerro.mitemperature2alt.coreUi.components.ThermometerBox
import com.klewerro.mitemperature2alt.coreUi.components.ThermometerTemperatureBox
import com.klewerro.mitemperature2alt.coreUi.theme.MiTemperature2AltTheme
import com.klewerro.mitemperature2alt.domain.model.RssiStrength
import com.klewerro.mitemperature2alt.domain.model.Thermometer
import com.klewerro.mitemperature2alt.domain.model.ThermometerConnectionStatus

@Composable
fun MainScreenThermometerBox(
    thermometer: Thermometer,
    onConnectClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(268.dp)
    ) {
        when (thermometer.thermometerConnectionStatus) {
            ThermometerConnectionStatus.CONNECTING -> {
                ThermometerBox {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = thermometer.name,
                            style = MaterialTheme.typography.h2,
                            textAlign = TextAlign.Center
                        )
                        Text(text = thermometer.address)
                        Spacer(Modifier.height(spacing.spaceLarge))
                        CircularProgressIndicator()
                        Text(
                            text = stringResource(R.string.connecting).uppercase(),
                            style = MaterialTheme.typography.h3,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            ThermometerConnectionStatus.CONNECTED -> {
                ThermometerTemperatureBox(
                    thermometer.temperature,
                    thermometer.humidity,
                    thermometer.voltage,
                    modifier.fillMaxSize(),
                    nameComposable = {
                        Text(text = thermometer.name)
                    }
                )
            }
            ThermometerConnectionStatus.DISCONNECTING,
            ThermometerConnectionStatus.DISCONNECTED -> {
                ThermometerBox {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = thermometer.name,
                            style = MaterialTheme.typography.h2,
                            textAlign = TextAlign.Center
                        )
                        Text(text = thermometer.address)
                        Spacer(Modifier.height(spacing.spaceLarge))
                        Text(
                            text = stringResource(R.string.disconnected).uppercase(),
                            style = MaterialTheme.typography.h3,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(spacing.spaceLarge))
                        OutlinedButton(onClick = onConnectClick) {
                            Text(text = stringResource(R.string.connect))
                        }
                    }
                }
            }
        }
    }
}

// region previews
@Preview(name = "Connected", showBackground = true)
@Preview(
    name = "Connected Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun MainScreenThermometerBoxPreview() {
    MiTemperature2AltTheme {
        MainScreenThermometerBox(
            Thermometer(
                name = "name",
                address = "00:00:00:00:00:00",
                temperature = 21.1f,
                humidity = 56,
                voltage = 1.231f,
                rssi = RssiStrength.GOOD,
                thermometerConnectionStatus = ThermometerConnectionStatus.CONNECTED
            ),
            onConnectClick = {}
        )
    }
}

@Preview(name = "Disconnected", showBackground = true)
@Preview(
    name = "Disconnected Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun MainScreenThermometerBoxDisconnectedPreview() {
    MiTemperature2AltTheme {
        MainScreenThermometerBox(
            thermometer = Thermometer(
                name = "name",
                address = "00:00:00:00:00:00",
                temperature = 21.1f,
                humidity = 56,
                voltage = 1.231f,
                rssi = RssiStrength.UNKNOWN,
                thermometerConnectionStatus = ThermometerConnectionStatus.DISCONNECTED
            ),
            onConnectClick = {}
        )
    }
}

@Preview(name = "Connecting", showBackground = true)
@Preview(
    name = "Connecting Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun MainScreenThermometerBoxConnectingPreview() {
    MiTemperature2AltTheme {
        MainScreenThermometerBox(
            thermometer = Thermometer(
                name = "name",
                address = "00:00:00:00:00:00",
                temperature = 21.1f,
                humidity = 56,
                voltage = 1.231f,
                rssi = RssiStrength.UNKNOWN,
                thermometerConnectionStatus = ThermometerConnectionStatus.CONNECTING
            ),
            onConnectClick = {}
        )
    }
}
// endregion
