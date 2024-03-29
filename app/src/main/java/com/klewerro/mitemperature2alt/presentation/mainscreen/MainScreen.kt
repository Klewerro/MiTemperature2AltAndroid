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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.klewerro.mitemperature2alt.presentation.mainscreen.components.NoConnectedThermometersInformation
import com.klewerro.mitemperature2alt.presentation.mainscreen.components.SavedThermometerBox
import com.klewerro.mitemperature2alt.presentation.mainscreen.components.ThermometerBox
import com.klewerro.mitemperature2alt.ui.LocalSpacing

@Composable
fun MainScreen(viewModel: BleOperationsViewModel, modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current
    val connectedDevices by viewModel.connectedDevices.collectAsStateWithLifecycle()
    val savedThermometers by viewModel.savedThermometers.collectAsStateWithLifecycle()

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
        if (connectedDevices.isEmpty() && savedThermometers.isEmpty()) {
            NoConnectedThermometersInformation()
        } else {
            LazyColumn(
                modifier
                    .fillMaxWidth()
            ) {
                items(savedThermometers) { savedThermometer ->
                    SavedThermometerBox(
                        savedThermometer = savedThermometer,
                        modifier = Modifier
                            .padding(vertical = spacing.spaceNormal)
                            .clickable { isShowingDialog = true }
                            .alpha(.5f)
                    )
                }

                items(connectedDevices) { thermometerDevice ->
                    ThermometerBox(
                        thermometerDevice = thermometerDevice,
                        onRefreshClick = {
                            viewModel.getStatusForDevice(thermometerDevice.address)
                        },
                        onSubscribeClick = {
                            viewModel.subscribeForDeviceStatusUpdates(thermometerDevice.address)
                        },
                        onSaveClick = {
                            isShowingDialog = true
                            viewModel.saveThermometer(thermometerDevice.address)
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
