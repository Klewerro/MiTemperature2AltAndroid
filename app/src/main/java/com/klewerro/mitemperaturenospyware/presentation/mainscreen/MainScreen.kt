package com.klewerro.mitemperaturenospyware.presentation.mainscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.klewerro.mitemperaturenospyware.presentation.mainscreen.components.NoConnectedThermometersInformation
import com.klewerro.mitemperaturenospyware.presentation.mainscreen.components.ThermometerBox
import com.klewerro.mitemperaturenospyware.ui.LocalSpacing

@Composable
fun MainScreen(
    viewModel: BleOperationsViewModel,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    val connectedDevices by viewModel.connectedDevices.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = spacing.spaceExtraLarge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (connectedDevices.isEmpty()) {
            NoConnectedThermometersInformation()
        } else {
            LazyColumn(
                modifier
                    .fillMaxWidth()
            ) {
                items(connectedDevices) { thermometerUiDevice ->
                    ThermometerBox(
                        name = thermometerUiDevice.address,
                        thermometerStatus = thermometerUiDevice.status,
                        onRefreshClick = {
                            viewModel.getStatusForDevice(thermometerUiDevice.address)
                        },
                        onSubscribeClick = {
                            viewModel.subscribeForDeviceStatusUpdates(thermometerUiDevice.address)
                        },
                        modifier = Modifier.padding(vertical = spacing.spaceNormal)
                    )
                }
            }
        }
    }
}
