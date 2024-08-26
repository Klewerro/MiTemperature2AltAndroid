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
import com.klewerro.mitemperature2alt.presentation.bottomSheet.components.BottomSheetProgressType
import com.klewerro.mitemperature2alt.presentation.mainscreen.ThermometerOperationType

@Composable
fun BottomSheetContent(
    thermometerOperationType: ThermometerOperationType,
    thermometers: List<Thermometer>,
    thermometerWithRunningOperation: Thermometer? = null,
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
            isInProgress = thermometerOperationType !is ThermometerOperationType.Idle,
            progressType = when (thermometerOperationType) {
                ThermometerOperationType.Idle, is ThermometerOperationType.ConnectingToDevice ->
                    BottomSheetProgressType.Indeterminate

                is ThermometerOperationType.RetrievingHourlyRecords ->
                    BottomSheetProgressType.Determinate(
                        thermometerOperationType.currentRecordNumber,
                        thermometerOperationType.numberOrRecords
                    )
            },
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

                is ThermometerOperationType.RetrievingHourlyRecords -> stringResource(
                    R.string.getting_hourly_records_for_string_int_of_int,
                    thermometerOperationType.thermometer.name,
                    thermometerOperationType.currentRecordNumber,
                    thermometerOperationType.numberOrRecords
                )
            },
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        LazyColumn {
            items(thermometers) { thermometer ->
                val isFetching = thermometerWithRunningOperation?.address == thermometer.address
                BottomSheetThermometerItem(
                    thermometer = thermometer,
                    isSynchronizing = isFetching,
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

@Preview(showBackground = true)
@Composable
fun BottomSheetContentGettingHourlyRecordsPreview() {
    MiTemperature2AltTheme {
        BottomSheetContent(
            thermometerOperationType = ThermometerOperationType.RetrievingHourlyRecords(
                ThermometerPreviewModels.thermometer,
                1,
                32
            ),
            thermometers = listOf(
                ThermometerPreviewModels.thermometer,
                ThermometerPreviewModels.thermometer,
                ThermometerPreviewModels.thermometer
            ),
            onConnectThermometerClick = {}
        )
    }
}
