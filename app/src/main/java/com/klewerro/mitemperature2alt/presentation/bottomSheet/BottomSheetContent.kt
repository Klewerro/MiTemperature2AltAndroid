package com.klewerro.mitemperature2alt.presentation.bottomSheet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.klewerro.mitemperature2alt.coreUi.LocalSpacing
import com.klewerro.mitemperature2alt.coreUi.previewModel.ThermometerPreviewModels
import com.klewerro.mitemperature2alt.coreUi.theme.MiTemperature2AltTheme
import com.klewerro.mitemperature2alt.domain.model.Thermometer
import com.klewerro.mitemperature2alt.presentation.bottomSheet.components.BottomSheetAnchor

@Composable
fun BottomSheetContent(
    isOperationPending: Boolean,
    thermometers: List<Thermometer>,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
    ) {
        BottomSheetAnchor(modifier = Modifier.align(Alignment.CenterHorizontally))
        AnimatedVisibility(visible = isOperationPending) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(spacing.spaceSmall)
                    .clip(RoundedCornerShape(spacing.radiusSmall))
            )
        }

        Text(
            text = "Status text...",
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        LazyColumn {
            items(thermometers) { thermometer ->
                BottomSheetThermometerItem(
                    thermometer = thermometer,
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
fun BottomSheetContentPreview(modifier: Modifier = Modifier) {
    MiTemperature2AltTheme {
        BottomSheetContent(
            false,
            listOf(
                ThermometerPreviewModels.thermometer,
                ThermometerPreviewModels.thermometer,
                ThermometerPreviewModels.thermometer

            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BottomSheetContentOperationPendingPreview(modifier: Modifier = Modifier) {
    MiTemperature2AltTheme {
        BottomSheetContent(
            false,
            listOf(
                ThermometerPreviewModels.thermometer,
                ThermometerPreviewModels.thermometer,
                ThermometerPreviewModels.thermometer

            )
        )
    }
}
