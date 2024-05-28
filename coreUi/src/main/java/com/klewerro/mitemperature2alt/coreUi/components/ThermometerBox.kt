package com.klewerro.mitemperature2alt.coreUi.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.klewerro.mitemperature2alt.coreUi.LocalSpacing
import com.klewerro.mitemperature2alt.coreUi.theme.MiTemperature2AltTheme

@Composable
fun ThermometerBox(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val spacing = LocalSpacing.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(268.dp)
            .shadow(
                spacing.elevationShadowNormal,
                shape = RoundedCornerShape(spacing.spaceNormal)
            )
            .clip(RoundedCornerShape(spacing.radiusNormal))
            .background(MaterialTheme.colors.background)
            .testTag("thermometerBox")
    ) {
        Box(
            modifier = Modifier
                .padding(18.dp)
                .clip(RoundedCornerShape(spacing.radiusNormal))
                .background(Color.LightGray)
        ) {
            content()
        }
    }
}

@PreviewLightDark
@Composable
private fun ThermometerBoxPreview() {
    MiTemperature2AltTheme {
        ThermometerBox {
            Text(
                text = "Test",
                modifier = Modifier.fillMaxSize(),
                textAlign = TextAlign.Center
            )
        }
    }
}
