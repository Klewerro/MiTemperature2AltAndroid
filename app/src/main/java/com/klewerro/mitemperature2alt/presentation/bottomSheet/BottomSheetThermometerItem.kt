package com.klewerro.mitemperature2alt.presentation.bottomSheet

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import com.klewerro.mitemperature2alt.coreUi.LocalSpacing
import com.klewerro.mitemperature2alt.coreUi.previewModel.ThermometerPreviewModels
import com.klewerro.mitemperature2alt.coreUi.theme.MiTemperature2AltTheme
import com.klewerro.mitemperature2alt.domain.model.RssiStrength
import com.klewerro.mitemperature2alt.domain.model.Thermometer
import com.klewerro.mitemperature2alt.domain.model.ThermometerConnectionStatus
import com.klewerro.mitemperature2alt.presentation.bottomSheet.components.BottomSheetThermometerStatus

@Composable
fun BottomSheetThermometerItem(thermometer: Thermometer, modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current

    Card(modifier = modifier) {
        val constraints = ConstraintSet {
            val thermometerNameText = createRefFor("thermometerNameText")
            val bottomSheetThermometerStatus = createRefFor("bottomSheetThermometerStatus")
            val addressText = createRefFor("addressText")
            val voltageText = createRefFor("voltageText")

            constrain(thermometerNameText) {
                width = Dimension.fillToConstraints
            }
            constrain(bottomSheetThermometerStatus) {
                end.linkTo(parent.end)
            }
            constrain(addressText) {
                start.linkTo(parent.start)
                top.linkTo(bottomSheetThermometerStatus.bottom)
            }
            constrain(voltageText) {
                end.linkTo(parent.end)
                top.linkTo(bottomSheetThermometerStatus.bottom)
            }
        }

        ConstraintLayout(
            constraintSet = constraints,
            modifier = Modifier
                .padding(spacing.radiusNormal)
        ) {
            Text(
                text = thermometer.name,
                modifier = Modifier.layoutId("thermometerNameText")
            )
            BottomSheetThermometerStatus(
                thermometer.rssi,
                thermometer.thermometerConnectionStatus,
                modifier = Modifier.layoutId("bottomSheetThermometerStatus")
            )
            Text(
                thermometer.address,
                Modifier.layoutId("addressText")
            )
            Text(
                text = thermometer.voltage.toString(),
                modifier = Modifier.layoutId("voltageText")
            )
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
                thermometerConnectionStatus = ThermometerConnectionStatus.CONNECTING,
            ),
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
            modifier = Modifier.fillMaxWidth()
        )
    }
}
// endregion
