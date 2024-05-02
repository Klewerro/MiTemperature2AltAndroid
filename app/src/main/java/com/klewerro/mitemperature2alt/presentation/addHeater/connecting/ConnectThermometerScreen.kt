package com.klewerro.mitemperature2alt.presentation.addHeater.connecting

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.klewerro.mitemperature2alt.R
import com.klewerro.mitemperature2alt.domain.model.ConnectionStatus
import com.klewerro.mitemperature2alt.ui.theme.MiTemperature2AltTheme

@Composable
fun ConnectThermometerScreen(
    viewModel: ConnectThermometerViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewModel.connectToDevice()
    }

    viewModel.saveThermometerAddress?.let { // Todo: Error catching
        ConnectThermometerScreenContent(it, state, modifier)
    }
}

@Composable
private fun ConnectThermometerScreenContent(
    thermometerAddress: String,
    state: ConnectThermometerState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(visible = state.connectionStatus == ConnectionStatus.CONNECTING) {
            Box(contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(id = R.drawable.thermometer_image),
                    contentDescription = "Humidity sensor",
                    modifier = modifier
                        .size(150.dp)
                )
                CircularProgressIndicator(
                    modifier = modifier.size(182.dp),
                    strokeWidth = 6.dp
                )
            }
        }
        Text(
            text = when (state.connectionStatus) {
                ConnectionStatus.NOT_CONNECTED -> "Connecting error."
                ConnectionStatus.CONNECTING -> "Connecting to $thermometerAddress thermometer."
                ConnectionStatus.CONNECTED -> "Connected to $thermometerAddress thermometer."
            },

            style = MaterialTheme.typography.h3,
            modifier = Modifier.width(182.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ConnectThermometerScreenContentConnectingPreview() {
    MiTemperature2AltTheme {
        val state = ConnectThermometerState(
            ConnectionStatus.CONNECTING
        )
        ConnectThermometerScreenContent("00:00:00:00", state)
    }
}

@Preview(showBackground = true)
@Composable
private fun ConnectThermometerScreenContentPreview() {
    MiTemperature2AltTheme {
        val state = ConnectThermometerState(
            ConnectionStatus.NOT_CONNECTED
        )
        ConnectThermometerScreenContent("00:00:00:00", state)
    }
}
