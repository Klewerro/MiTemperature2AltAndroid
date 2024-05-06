package com.klewerro.mitemperature2alt.presentation.addHeater.connecting

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.klewerro.mitemperature2alt.R
import com.klewerro.mitemperature2alt.ui.theme.MiTemperature2AltTheme

@Composable
fun ConnectThermometerScreen(
    viewModel: ConnectThermometerViewModel,
    scaffoldState: ScaffoldState,
    onCriticalError: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = state.thermometerAddress) {
        if (state.thermometerAddress.isNotEmpty()) {
            viewModel.connectToDevice()
        }
    }

    val context = LocalContext.current
    LaunchedEffect(key1 = state.error) {
        state.error?.let {
            scaffoldState.snackbarHostState.showSnackbar(it.asString(context))
            onCriticalError()
        }
    }

    ConnectThermometerScreenContent(state, modifier)
}

@Composable
private fun ConnectThermometerScreenContent(
    state: ConnectThermometerState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(visible = state.isConnecting) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = modifier
                    .fillMaxWidth(0.5f)
                    .aspectRatio(1f)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.thermometer_image),
                    contentDescription = "Humidity sensor",
                    modifier = modifier.fillMaxSize(0.8f)

                )
                CircularProgressIndicator(
                    modifier = Modifier.fillMaxSize(),
                    strokeWidth = 6.dp
                )
            }
        }
        Text(
            text = state.error?.let {
                "Connecting error."
            } ?: run {
                if (state.isConnecting) {
                    "Connecting to ${state.thermometerAddress} thermometer."
                } else {
                    "Connected to ${state.thermometerAddress} thermometer."
                }
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
            thermometerAddress = "00:00:00:00",
            isConnecting = true
        )
        ConnectThermometerScreenContent(state)
    }
}

@Preview(showBackground = true)
@Composable
private fun ConnectThermometerScreenContentPreview() {
    MiTemperature2AltTheme {
        val state = ConnectThermometerState(
            thermometerAddress = "00:00:00:00",
            isConnecting = false
        )
        ConnectThermometerScreenContent(state)
    }
}
