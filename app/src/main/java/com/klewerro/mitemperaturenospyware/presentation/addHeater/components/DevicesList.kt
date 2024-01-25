package com.klewerro.mitemperaturenospyware.presentation.addHeater.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import com.klewerro.mitemperaturenospyware.R
import com.klewerro.mitemperaturenospyware.domain.model.ConnectionStatus
import com.klewerro.mitemperaturenospyware.domain.model.ThermometerDevice
import com.klewerro.mitemperaturenospyware.ui.LocalSpacing

@Composable
fun DevicesList(
    isScanningForDevices: Boolean,
    scannedDevices: List<ThermometerDevice>,
    onButtonClickWhenScanning: () -> Unit,
    onButtonClickWhenNotScanning: () -> Unit,
    onDeviceClick: (ThermometerDevice) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
            items(scannedDevices) { thermometerDevice ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(spacing.spaceSmall)
                        .shadow(
                            spacing.elevationShadowNormal,
                            shape = RoundedCornerShape(spacing.spaceNormal)
                        )
                        .clip(RoundedCornerShape(spacing.spaceNormal))
                        .background(MaterialTheme.colors.onPrimary)
                        .clickable { onDeviceClick(thermometerDevice) }
                        .padding(spacing.spaceSmall)
                ) {
                    Text(text = thermometerDevice.name)
                    Text(text = thermometerDevice.address)
                    Text(text = thermometerDevice.rssi.toString())
                    when (thermometerDevice.connectionStatus) {
                        ConnectionStatus.NOT_CONNECTED -> { /*Nothing*/ }
                        ConnectionStatus.CONNECTING -> {
                            Spacer(modifier = Modifier.height(spacing.spaceSmall))
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        }
                        ConnectionStatus.CONNECTED -> Text(
                            text = stringResource(R.string.connected),
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        }
    }
}
