package com.klewerro.mitemperature2alt.presentation.addThermometer.connecting

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.klewerro.mitemperature2alt.R
import com.klewerro.mitemperature2alt.presentation.addThermometer.ConnectThermometerEvent
import com.klewerro.mitemperature2alt.presentation.addThermometer.ConnectThermometerState
import com.klewerro.mitemperature2alt.presentation.addThermometer.ConnectThermometerViewModel
import com.klewerro.mitemperature2alt.presentation.addThermometer.ConnectingStatus
import com.klewerro.mitemperature2alt.ui.LocalSpacing
import com.klewerro.mitemperature2alt.ui.theme.MiTemperature2AltTheme
import kotlinx.coroutines.delay

private const val SCREEN_CHANGE_DELAY = 1_500L

@Composable
fun ThermometerConnectingScreen(
    viewModel: ConnectThermometerViewModel,
    onError: () -> Unit,
    onDeviceConnected: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = state.thermometerAddress) {
        if (state.thermometerAddress.isNotEmpty()) {
            viewModel.onEvent(ConnectThermometerEvent.ConnectToDevice)
        }
    }
    LaunchedEffect(key1 = state.connectingStatus) {
        with(state.connectingStatus) {
            if (this == ConnectingStatus.CONNECTED) {
                delay(SCREEN_CHANGE_DELAY)
                onDeviceConnected()
            }
        }
    }

    ThermometerConnectingScreenContent(state, onError, modifier)
}

@Composable
private fun ThermometerConnectingScreenContent(
    state: ConnectThermometerState,
    onErrorClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val spacing = LocalSpacing.current

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedContent(
            targetState = state.connectingStatus,
            modifier = modifier
                .fillMaxWidth(0.5f)
                .aspectRatio(1f),
            transitionSpec = {
                slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy
                    )
                ).togetherWith(
                    fadeOut()
                )
            },
            content = { connectingStatus ->
                when (connectingStatus) {
                    ConnectingStatus.NOT_CONNECTING -> {
                        Box(modifier = Modifier.fillMaxSize())
                    }
                    ConnectingStatus.CONNECTING -> {
                        ProgressImage()
                    }
                    ConnectingStatus.CONNECTED, ConnectingStatus.ERROR -> {
                        Image(
                            painter = painterResource(
                                id = if (connectingStatus == ConnectingStatus.CONNECTED) {
                                    R.drawable.ic_check_circle_24
                                } else {
                                    R.drawable.ic_round_error_24
                                }
                            ),
                            contentDescription = stringResource(
                                if (connectingStatus == ConnectingStatus.CONNECTED) {
                                    R.string.thermometer_connected
                                } else {
                                    R.string.connecting_thermometer_error
                                }
                            ),
                            colorFilter = ColorFilter.tint(
                                if (connectingStatus == ConnectingStatus.CONNECTED) {
                                    Color.Green
                                } else {
                                    Color.Red
                                }
                            ),
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        )

        Text(
            text = when (state.connectingStatus) {
                ConnectingStatus.NOT_CONNECTING -> ""
                ConnectingStatus.CONNECTING -> stringResource(
                    id = R.string.connecting_to_ADDRESS_thermometer,
                    state.thermometerAddress
                )
                ConnectingStatus.CONNECTED -> stringResource(
                    id = R.string.connected_to_ADDRESS_thermometer,
                    state.thermometerAddress
                )
                ConnectingStatus.ERROR -> state.error?.asString(
                    context
                ) ?: stringResource(id = R.string.unexpected_error_occurred_try_again)
            },
            style = MaterialTheme.typography.h3,
            modifier = Modifier.padding(spacing.spaceScreen),
            textAlign = TextAlign.Center
        )
        AnimatedVisibility(visible = state.connectingStatus == ConnectingStatus.ERROR) {
            OutlinedButton(onClick = onErrorClick) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        }
    }
}

@Composable
private fun ProgressImage(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth(0.5f)
            .aspectRatio(1f)
    ) {
        Image(
            painter = painterResource(id = R.drawable.thermometer_image),
            contentDescription = stringResource(R.string.humidity_sensor),
            modifier = modifier.fillMaxSize(0.8f)

        )
        CircularProgressIndicator(
            modifier = Modifier.fillMaxSize(),
            strokeWidth = 6.dp
        )
    }
}

// region previews
@Preview(showBackground = true)
@Composable
private fun ThermometerConnectingScreenContentConnectingPreview() {
    MiTemperature2AltTheme {
        val state = ConnectThermometerState(
            thermometerAddress = "00:00:00:00",
            connectingStatus = ConnectingStatus.CONNECTING
        )
        ThermometerConnectingScreenContent(state, {})
    }
}

@Preview(showBackground = true)
@Composable
private fun ThermometerConnectingScreenContentConnectedPreview() {
    MiTemperature2AltTheme {
        val state = ConnectThermometerState(
            thermometerAddress = "00:00:00:00",
            connectingStatus = ConnectingStatus.CONNECTED
        )
        ThermometerConnectingScreenContent(state, {})
    }
}

@Preview(showBackground = true)
@Composable
private fun ThermometerConnectingScreenContentErrorPreview() {
    MiTemperature2AltTheme {
        val state = ConnectThermometerState(
            thermometerAddress = "00:00:00:00",
            connectingStatus = ConnectingStatus.ERROR
        )
        ThermometerConnectingScreenContent(state, {})
    }
}
// endregion
