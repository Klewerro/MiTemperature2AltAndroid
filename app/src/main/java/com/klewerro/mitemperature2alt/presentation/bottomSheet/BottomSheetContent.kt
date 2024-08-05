package com.klewerro.mitemperature2alt.presentation.bottomSheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.klewerro.mitemperature2alt.coreUi.LocalSpacing
import com.klewerro.mitemperature2alt.coreUi.R
import com.klewerro.mitemperature2alt.coreUi.previewModel.ThermometerPreviewModels
import com.klewerro.mitemperature2alt.coreUi.theme.MiTemperature2AltTheme
import com.klewerro.mitemperature2alt.domain.model.Thermometer
import com.klewerro.mitemperature2alt.domain.model.ThermometerConnectionStatus
import com.klewerro.mitemperature2alt.presentation.bottomSheet.components.BottomSheetProgressAnchor
import com.klewerro.mitemperature2alt.presentation.mainscreen.ThermometerOperationType

@Composable
fun BottomSheetContent(
    thermometerOperationType: ThermometerOperationType,
    thermometers: List<Thermometer>,
    onConnectThermometerClick: (Thermometer) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
    ) {
        BottomSheetProgressAnchor(
            thermometerOperationType is ThermometerOperationType.ConnectingToDevice,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Text(
            text = when (thermometerOperationType) {
                is ThermometerOperationType.Idle -> {
                    val connectedThermometersCount = thermometers.count {
                        it.thermometerConnectionStatus == ThermometerConnectionStatus.CONNECTED
                    }
                    stringResource(
                        R.string.string_of_string_thermometers_connected,
                        connectedThermometersCount,
                        thermometers.size
                    )
                }

                is ThermometerOperationType.ConnectingToDevice -> stringResource(
                    R.string.connecting_to_string,
                    thermometerOperationType.thermometerName
                )
            },
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        LazyColumn {
            items(thermometers) { thermometer ->
                BottomSheetThermometerItem(
                    thermometer = thermometer,
                    onConnectButtonClick = {
                        onConnectThermometerClick(thermometer)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(spacing.spaceSmall)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomSheetContentPreview() {
    MiTemperature2AltTheme {
        BottomSheetContent(
            thermometerOperationType = ThermometerOperationType.Idle,
            thermometers = listOf(
                ThermometerPreviewModels.thermometer,
                ThermometerPreviewModels.thermometer,
                ThermometerPreviewModels.thermometer
            ),
            onConnectThermometerClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BottomSheetContentOperationPendingPreview() {
    MiTemperature2AltTheme {
        BottomSheetContent(
            thermometerOperationType = ThermometerOperationType.ConnectingToDevice("Device_name"),
            thermometers = listOf(
                ThermometerPreviewModels.thermometer,
                ThermometerPreviewModels.thermometer,
                ThermometerPreviewModels.thermometer
            ),
            onConnectThermometerClick = {}
        )
    }
}
