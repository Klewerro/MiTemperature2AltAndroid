package com.klewerro.mitemperature2alt.presentation.mainscreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.klewerro.mitemperature2alt.presentation.mainscreen.components.NoConnectedThermometersInformation
import com.klewerro.mitemperature2alt.presentation.mainscreen.components.SavedThermometerBox
import com.klewerro.mitemperature2alt.presentation.mainscreen.components.ThermometerBox
import com.klewerro.mitemperature2alt.ui.LocalSpacing

@Composable
fun MainScreen(
    state: BleOperationsState,
    onEvent: (BleOperationsEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current

    // Todo: TEMP!
    var isShowingDialog by rememberSaveable {
        mutableStateOf(false)
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
                            .clickable { isShowingDialog = true }
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
                            isShowingDialog = true
                            onEvent(BleOperationsEvent.SaveThermometer(thermometerDevice.address))
                        },
                        modifier = Modifier.padding(vertical = spacing.spaceNormal)
                    )
                }
            }
        }

        if (isShowingDialog) {
            ThermometerNameDialog(
                onDismiss = {
                    isShowingDialog = false
                },
                onSave = {
                    isShowingDialog = false
                },
                onTextValueChange = {
                }
            )
        }
    }
}
