package com.klewerro.mitemperature2alt.presentation.bottomSheet

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
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

@Composable
fun BottomSheetThermometerItem(
    thermometer: Thermometer,
    onConnectButtonClick: () -> Unit,
    onThermometerCancelButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSynchronizing: Boolean = false
) {
    val spacing = LocalSpacing.current

    Card(modifier = modifier) {
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

// region Previews
@Preview(showBackground = true)
@Composable
private fun BottomSheetThermometerItemPreview() {
    MiTemperature2AltTheme {
        BottomSheetThermometerItem(
            thermometer = ThermometerPreviewModels.thermometer,
            onConnectButtonClick = {},
            onThermometerCancelButtonClick = {},
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
            modifier = Modifier.fillMaxWidth()
        )
    }
}
// endregion
