package com.klewerro.mitemperature2alt.presentation.bottomSheet

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import com.klewerro.mitemperature2alt.coreUi.LocalSpacing
import com.klewerro.mitemperature2alt.coreUi.R
import com.klewerro.mitemperature2alt.coreUi.previewModel.ThermometerPreviewModels
import com.klewerro.mitemperature2alt.coreUi.theme.MiTemperature2AltTheme
import com.klewerro.mitemperature2alt.coreUi.util.alphaDisabled
import com.klewerro.mitemperature2alt.domain.model.RssiStrength
import com.klewerro.mitemperature2alt.domain.model.Thermometer
import com.klewerro.mitemperature2alt.domain.model.ThermometerConnectionStatus
import com.klewerro.mitemperature2alt.presentation.bottomSheet.components.BottomSheetThermometerStatus
import de.charlex.compose.RevealDirection
import de.charlex.compose.RevealState
import de.charlex.compose.RevealSwipe
import de.charlex.compose.RevealValue
import de.charlex.compose.rememberRevealState

@Composable
fun BottomSheetThermometerItem(
    thermometer: Thermometer,
    onConnectButtonClick: () -> Unit,
    onThermometerCancelButtonClick: () -> Unit,
    onSyncClick: () -> Unit,
    onDisconnectClick: () -> Unit,
    modifier: Modifier = Modifier,
    isClickingEnabled: Boolean,
    isSynchronizing: Boolean = false,
    revealState: RevealState = rememberRevealState(
        maxRevealDp = 120.dp,
        directions = setOf(
            RevealDirection.StartToEnd,
            RevealDirection.EndToStart
        ),
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    ),
    shape: CornerBasedShape = RoundedCornerShape(LocalSpacing.current.radiusSmall)
) {
    val spacing = LocalSpacing.current
    val enableSwipe = thermometer.thermometerConnectionStatus ==
        ThermometerConnectionStatus.CONNECTED &&
        !isSynchronizing

    RevealSwipe(
        state = revealState,
        enableSwipe = enableSwipe,
        backgroundStartActionLabel = "backgroundStartActionLabel",
        backgroundEndActionLabel = "backgroundEndActionLabel",
        shape = shape,
        backgroundCardStartColor = com.klewerro.mitemperature2alt.coreUi.theme.BluePowdery,
        backgroundCardEndColor = com.klewerro.mitemperature2alt.coreUi.theme.RedPowdery,
        hiddenContentStart = {
            HiddenContent(
                text = "26.12.2016",
                imageVector = Icons.Default.Refresh,
                contentDescription = stringResource(R.string.last_synchronization_datetime)
            )
        },
        hiddenContentEnd = {
            HiddenContent(
                text = stringResource(R.string.disconnect),
                imageVector = Icons.Default.Close
            )
        },
        onBackgroundStartClick = {
            onSyncClick()
            true
        },
        onBackgroundEndClick = {
            onDisconnectClick()
            true
        },
        modifier = modifier

    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = shape
        ) {
            val constraints = ConstraintSet {
                val thermometerNameText = createRefFor("thermometerNameText")
                val bottomSheetThermometerStatus = createRefFor("bottomSheetThermometerStatus")
                val addressText = createRefFor("addressText")
                val voltageText = createRefFor("voltageText")
                val connectButton = createRefFor("connectButton")

                constrain(thermometerNameText) {
                    width = Dimension.fillToConstraints
                }
                constrain(bottomSheetThermometerStatus) {
                    end.linkTo(parent.end)
                    if (thermometer.thermometerConnectionStatus ==
                        ThermometerConnectionStatus.CONNECTING
                    ) {
                        centerVerticallyTo(parent)
                    }
                }
                constrain(addressText) {
                    start.linkTo(parent.start)
                    top.linkTo(thermometerNameText.bottom)
                }
                constrain(voltageText) {
                    end.linkTo(parent.end)
                    top.linkTo(bottomSheetThermometerStatus.bottom)
                }
                constrain(connectButton) {
                    end.linkTo(parent.end)
                    centerVerticallyTo(parent)
                }
            }

            ConstraintLayout(
                constraintSet = constraints,
                modifier = Modifier
                    .padding(spacing.radiusNormal)
            ) {
                Text(
                    text = thermometer.name,
                    modifier = Modifier
                        .layoutId("thermometerNameText")
                        .alphaDisabled(
                            thermometer.thermometerConnectionStatus ==
                                ThermometerConnectionStatus.DISCONNECTED
                        )
                )
                Text(
                    thermometer.address,
                    Modifier
                        .layoutId("addressText")
                        .alphaDisabled(
                            thermometer.thermometerConnectionStatus ==
                                ThermometerConnectionStatus.DISCONNECTED
                        )
                )

                if (thermometer.thermometerConnectionStatus ==
                    ThermometerConnectionStatus.DISCONNECTED
                ) {
                    Button(
                        onClick = onConnectButtonClick,
                        enabled = isClickingEnabled,
                        modifier = Modifier.layoutId("connectButton")
                    ) {
                        Text(text = stringResource(id = R.string.connect))
                    }
                } else {
                    BottomSheetThermometerStatus(
                        thermometer.rssi,
                        thermometer.thermometerConnectionStatus,
                        isSynchronizing = isSynchronizing,
                        modifier = Modifier.layoutId("bottomSheetThermometerStatus"),
                        onCancelClick = onThermometerCancelButtonClick
                    )
                }
                if (thermometer.thermometerConnectionStatus ==
                    ThermometerConnectionStatus.CONNECTED
                ) {
                    Text(
                        text = thermometer.voltage.toString() + "V",
                        modifier = Modifier.layoutId("voltageText")
                    )
                }
            }
        }
    }
}

@Composable
private fun HiddenContent(
    text: String,
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    contentDescription: String = text
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(horizontal = 8.dp)
    ) {
        Icon(imageVector = imageVector, contentDescription = text)
        Text(text = text)
    }
}

// region Previews
@Preview(showBackground = true)
@Composable
private fun BottomSheetThermometerItemPreview() {
    MiTemperature2AltTheme {
        BottomSheetThermometerItem(
            thermometer = ThermometerPreviewModels.thermometer,
            onConnectButtonClick = {},
            onThermometerCancelButtonClick = {},
            onSyncClick = {},
            onDisconnectClick = {},
            isClickingEnabled = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomSheetThermometerItemPreviewThermometerConnectionStatusConnecting() {
    MiTemperature2AltTheme {
        BottomSheetThermometerItem(
            thermometer = ThermometerPreviewModels.thermometer.copy(
                thermometerConnectionStatus = ThermometerConnectionStatus.CONNECTING
            ),
            onConnectButtonClick = {},
            onThermometerCancelButtonClick = {},
            onSyncClick = {},
            onDisconnectClick = {},
            isClickingEnabled = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomSheetThermometerItemPreviewThermometerConnectionStatusConnected() {
    MiTemperature2AltTheme {
        BottomSheetThermometerItem(
            thermometer = ThermometerPreviewModels.thermometer.copy(
                thermometerConnectionStatus = ThermometerConnectionStatus.CONNECTED
            ),
            onConnectButtonClick = {},
            onThermometerCancelButtonClick = {},
            onSyncClick = {},
            onDisconnectClick = {},
            isClickingEnabled = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomSheetThermometerItemPreviewThermometerConnectionStatusDisconnecting() {
    MiTemperature2AltTheme {
        BottomSheetThermometerItem(
            thermometer = ThermometerPreviewModels.thermometer.copy(
                thermometerConnectionStatus = ThermometerConnectionStatus.DISCONNECTING
            ),
            onConnectButtonClick = {},
            onThermometerCancelButtonClick = {},
            onSyncClick = {},
            onDisconnectClick = {},
            isClickingEnabled = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomSheetThermometerItemPreviewThermometerConnectionStatusDisconnected() {
    MiTemperature2AltTheme {
        BottomSheetThermometerItem(
            thermometer = ThermometerPreviewModels.thermometer.copy(
                thermometerConnectionStatus = ThermometerConnectionStatus.DISCONNECTED
            ),
            onConnectButtonClick = {},
            onThermometerCancelButtonClick = {},
            onSyncClick = {},
            onDisconnectClick = {},
            isClickingEnabled = false,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomSheetThermometerItemPreviewRssiExcellent() {
    MiTemperature2AltTheme {
        BottomSheetThermometerItem(
            thermometer = ThermometerPreviewModels.thermometer.copy(
                rssi = RssiStrength.EXCELLENT
            ),
            onConnectButtonClick = {},
            onThermometerCancelButtonClick = {},
            onSyncClick = {},
            onDisconnectClick = {},
            isClickingEnabled = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomSheetThermometerItemPreviewRssiVeryGood() {
    MiTemperature2AltTheme {
        BottomSheetThermometerItem(
            thermometer = ThermometerPreviewModels.thermometer.copy(
                rssi = RssiStrength.VERY_GOOD
            ),
            onConnectButtonClick = {},
            onThermometerCancelButtonClick = {},
            onSyncClick = {},
            onDisconnectClick = {},
            isClickingEnabled = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomSheetThermometerItemPreviewRssiGood() {
    MiTemperature2AltTheme {
        BottomSheetThermometerItem(
            thermometer = ThermometerPreviewModels.thermometer.copy(
                rssi = RssiStrength.GOOD
            ),
            onConnectButtonClick = {},
            onThermometerCancelButtonClick = {},
            onSyncClick = {},
            onDisconnectClick = {},
            isClickingEnabled = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomSheetThermometerItemPreviewRssiPoor() {
    MiTemperature2AltTheme {
        BottomSheetThermometerItem(
            thermometer = ThermometerPreviewModels.thermometer.copy(
                rssi = RssiStrength.POOR
            ),
            onConnectButtonClick = {},
            onThermometerCancelButtonClick = {},
            onSyncClick = {},
            onDisconnectClick = {},
            isClickingEnabled = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomSheetThermometerItemPreviewRssiUnusable() {
    MiTemperature2AltTheme {
        BottomSheetThermometerItem(
            thermometer = ThermometerPreviewModels.thermometer.copy(
                rssi = RssiStrength.UNUSABLE
            ),
            onConnectButtonClick = {},
            onThermometerCancelButtonClick = {},
            onSyncClick = {},
            onDisconnectClick = {},
            isClickingEnabled = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomSheetThermometerItemPreviewRssiUnknown() {
    MiTemperature2AltTheme {
        BottomSheetThermometerItem(
            thermometer = ThermometerPreviewModels.thermometer.copy(
                rssi = RssiStrength.UNKNOWN
            ),
            onConnectButtonClick = {},
            onThermometerCancelButtonClick = {},
            onSyncClick = {},
            onDisconnectClick = {},
            isClickingEnabled = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomSheetThermometerItemPerformingOperation() {
    MiTemperature2AltTheme {
        BottomSheetThermometerItem(
            thermometer = ThermometerPreviewModels.thermometer,
            isSynchronizing = true,
            onConnectButtonClick = {},
            onThermometerCancelButtonClick = {},
            onSyncClick = {},
            onDisconnectClick = {},
            isClickingEnabled = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomSheetThermometerItemLeftHiddenContentPreview() {
    MiTemperature2AltTheme {
        BottomSheetThermometerItem(
            thermometer = ThermometerPreviewModels.thermometer,
            onConnectButtonClick = {},
            onThermometerCancelButtonClick = {},
            onSyncClick = {},
            onDisconnectClick = {},
            isClickingEnabled = true,
            modifier = Modifier.fillMaxWidth(),
            revealState = rememberRevealState(
                maxRevealDp = 120.dp,
                directions = setOf(
                    RevealDirection.StartToEnd,
                    RevealDirection.EndToStart
                ),
                initialValue = RevealValue.FullyRevealedEnd
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomSheetThermometerItemRightHiddenContentPreview() {
    MiTemperature2AltTheme {
        BottomSheetThermometerItem(
            thermometer = ThermometerPreviewModels.thermometer,
            onConnectButtonClick = {},
            onThermometerCancelButtonClick = {},
            onSyncClick = {},
            onDisconnectClick = {},
            isClickingEnabled = true,
            modifier = Modifier.fillMaxWidth(),
            revealState = rememberRevealState(
                maxRevealDp = 120.dp,
                directions = setOf(
                    RevealDirection.StartToEnd,
                    RevealDirection.EndToStart
                ),
                initialValue = RevealValue.FullyRevealedStart
            )
        )
    }
}
// endregion
