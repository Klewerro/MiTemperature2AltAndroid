package com.klewerro.mitemperature2alt.presentation.mainscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.klewerro.mitemperature2alt.coreUi.LocalSpacing
import com.klewerro.mitemperature2alt.presentation.mainscreen.components.MainScreenThermometerBox
import com.klewerro.mitemperature2alt.presentation.mainscreen.components.NoConnectedThermometersInformation

@Composable
fun MainScreen(
    state: BleOperationsState,
    onEvent: (BleOperationsEvent) -> Unit,
    scaffoldState: ScaffoldState,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current

    LaunchedEffect(key1 = state.error) {
        state.error?.let { errorUiText ->
            scaffoldState.snackbarHostState
            scaffoldState.snackbarHostState.showSnackbar(
                message = errorUiText.asString(context)
            )
            onEvent(BleOperationsEvent.ErrorDismissed)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = spacing.spaceExtraLarge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (state.thermometers.isEmpty()) {
            NoConnectedThermometersInformation()
        } else {
            LazyColumn(
                modifier
                    .fillMaxWidth()
            ) {
                items(state.thermometers) { thermometer ->
                    MainScreenThermometerBox(
                        thermometer = thermometer,
                        onRefreshClick = {
                            onEvent(
                                BleOperationsEvent.GetStatusForDevice(thermometer.address)
                            )
                        },
                        onSubscribeClick = {
                            onEvent(
                                BleOperationsEvent.SubscribeForDeviceStatusUpdates(
                                    thermometer.address
                                )
                            )
                        },
                        onConnectClick = { deviceAddress ->
                            onEvent(
                                BleOperationsEvent.ConnectToDevice(deviceAddress)
                            )
                        },
                        modifier = Modifier.padding(vertical = spacing.spaceNormal)
                    )
                }
            }
        }
    }
}
