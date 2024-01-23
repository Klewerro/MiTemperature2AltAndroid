package com.klewerro.mitemperaturenospyware.presentation.addHeater.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.klewerro.mitemperaturenospyware.R
import com.klewerro.mitemperaturenospyware.presentation.model.ThermometerUiDevice
import com.klewerro.mitemperaturenospyware.ui.LocalSpacing

@Composable
fun DevicesList(
    isScanningForDevices: Boolean,
    scannedDevices: List<ThermometerUiDevice>,
    onButtonClickWhenScanning: () -> Unit,
    onButtonClickWhenNotScanning: () -> Unit,
    onDeviceClick: (ThermometerUiDevice) -> Unit
) {
    val spacing = LocalSpacing.current

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Button(
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
            onClick = {
                if (isScanningForDevices) {
                    onButtonClickWhenScanning()
                } else {
                    onButtonClickWhenNotScanning()
                }
            }
        ) {
            Text(
                text = stringResource(
                    if (isScanningForDevices) R.string.stop_scan else R.string.scan_for_devices
                )
            )
        }
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(scannedDevices) { thermometerUiDevice ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(spacing.spaceSmall)
                        .shadow(
                            4.dp,
                            shape = RoundedCornerShape(spacing.spaceNormal)
                        )
                        .clip(RoundedCornerShape(spacing.spaceNormal))
                        .background(MaterialTheme.colors.onPrimary)
                        .clickable { onDeviceClick(thermometerUiDevice) }
                        .padding(spacing.spaceSmall)
                ) {
                    Text(text = thermometerUiDevice.name)
                    Text(text = thermometerUiDevice.address)
                    Text(text = thermometerUiDevice.rssi.toString())
                    if (thermometerUiDevice.isConnected) {
                        Text(text = "CONNECTED")
                    }
                }
            }
        }
    }
}
