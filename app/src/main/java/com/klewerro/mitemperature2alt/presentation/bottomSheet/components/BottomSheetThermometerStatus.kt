package com.klewerro.mitemperature2alt.presentation.bottomSheet.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NetworkWifi1Bar
import androidx.compose.material.icons.filled.NetworkWifi2Bar
import androidx.compose.material.icons.filled.NetworkWifi3Bar
import androidx.compose.material.icons.filled.SignalWifi0Bar
import androidx.compose.material.icons.filled.SignalWifi4Bar
import androidx.compose.material.icons.filled.SignalWifiStatusbarConnectedNoInternet4
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.klewerro.mitemperature2alt.coreUi.R as RCore
import com.klewerro.mitemperature2alt.domain.model.RssiStrength
import com.klewerro.mitemperature2alt.domain.model.ThermometerConnectionStatus

@Composable
fun BottomSheetThermometerStatus(
    rssiStrength: RssiStrength,
    thermometerConnectionStatus: ThermometerConnectionStatus,
    modifier: Modifier = Modifier,
    isSynchronizing: Boolean = false
) {
    when {
        isSynchronizing -> ThermometerProgress(
            text = stringResource(RCore.string.syncing),
            modifier = modifier
        )

        thermometerConnectionStatus == ThermometerConnectionStatus.DISCONNECTED -> Text(
            text = stringResource(RCore.string.disconnected),
            modifier = modifier
        )

        thermometerConnectionStatus == ThermometerConnectionStatus.CONNECTING -> {
            ThermometerProgress(
                text = stringResource(RCore.string.connecting),
                modifier = modifier
            )
        }

        thermometerConnectionStatus == ThermometerConnectionStatus.CONNECTED -> {
            Row(modifier = modifier) {
                Image(
                    imageVector = when (rssiStrength) {
                        RssiStrength.EXCELLENT -> Icons.Default.SignalWifi4Bar
                        RssiStrength.VERY_GOOD -> Icons.Default.NetworkWifi3Bar
                        RssiStrength.GOOD -> Icons.Default.NetworkWifi2Bar
                        RssiStrength.POOR -> Icons.Default.NetworkWifi1Bar
                        RssiStrength.UNUSABLE -> Icons.Default.SignalWifi0Bar
                        RssiStrength.UNKNOWN -> Icons.Default.SignalWifiStatusbarConnectedNoInternet4
                    },
                    contentDescription = "Connection strength",
                    colorFilter = if (rssiStrength == RssiStrength.UNUSABLE ||
                        rssiStrength == RssiStrength.UNKNOWN
                    ) {
                        ColorFilter.tint(MaterialTheme.colors.error)
                    } else {
                        ColorFilter.tint(MaterialTheme.colors.primary)
                    },
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Text(
                    text = stringResource(RCore.string.connected),
                    color = MaterialTheme.colors.primary
                )
            }
        }

        thermometerConnectionStatus == ThermometerConnectionStatus.DISCONNECTING -> {
            Row(modifier = modifier) {
                CircularProgressIndicator(modifier = Modifier.padding(horizontal = 8.dp))
                Text(
                    text = stringResource(RCore.string.disconnecting),
                    color = MaterialTheme.colors.primary
                )
            }
        }
    }
}

@Composable
private fun ThermometerProgress(text: String, modifier: Modifier = Modifier) {
    var textSize by remember {
        mutableStateOf(IntSize.Zero)
    }
    Row(modifier = modifier) {
        CircularProgressIndicator(
            modifier = modifier
                .padding(horizontal = 8.dp)
                .size(with(LocalDensity.current) { textSize.height.toDp() })
        )
        Text(
            text = text,
            modifier = Modifier.onGloballyPositioned { layoutCoordinates ->
                textSize = layoutCoordinates.size
            },
            color = MaterialTheme.colors.primary
        )
    }
}
