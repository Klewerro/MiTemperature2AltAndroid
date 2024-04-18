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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import com.klewerro.mitemperature2alt.presentation.mainscreen.components.NoConnectedThermometersInformation
import com.klewerro.mitemperature2alt.presentation.mainscreen.components.SavedThermometerBox
import com.klewerro.mitemperature2alt.presentation.mainscreen.components.ThermometerBox
import com.klewerro.mitemperature2alt.ui.LocalSpacing

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
        if (state.connectedDevices.isEmpty() && state.savedThermometers.isEmpty()) {
            NoConnectedThermometersInformation()
        } else {
            LazyColumn(
                modifier
                    .fillMaxWidth()
            ) {
                items(state.savedThermometers) { savedThermometer ->
                    SavedThermometerBox(
                        savedThermometer = savedThermometer,
                        modifier = Modifier
                            .padding(vertical = spacing.spaceNormal)
                            .alpha(.5f)
                    )
                }

                items(state.connectedDevices) { thermometerDevice ->
                    ThermometerBox(
                        thermometerDevice = thermometerDevice,
                        onRefreshClick = {
                            onEvent(
                                BleOperationsEvent.GetStatusForDevice(thermometerDevice.address)
                            )
                        },
                        onSubscribeClick = {
                            onEvent(
                                BleOperationsEvent.SubscribeForDeviceStatusUpdates(
                                    thermometerDevice.address
                                )
                            )
                        },
                        onSaveClick = {
                            onEvent(
                                BleOperationsEvent.OpenSaveThermometer(thermometerDevice.address)
                            )
                        },
                        modifier = Modifier.padding(vertical = spacing.spaceNormal)
                    )
                }
            }
        }

        SaveThermometerDialog(
            isVisible = state.isShowingSaveDialog,
            onDismiss = {
                onEvent(BleOperationsEvent.CloseSaveThermometer)
            },
            onSave = { thermometerName ->
                onEvent(BleOperationsEvent.SaveThermometer(thermometerName))
            }
        )
    }
}
