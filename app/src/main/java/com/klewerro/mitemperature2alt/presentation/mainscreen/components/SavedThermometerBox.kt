package com.klewerro.mitemperature2alt.presentation.mainscreen.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.klewerro.mitemperature2alt.coreUi.LocalSpacing
import com.klewerro.mitemperature2alt.domain.model.SavedThermometer

@Composable
fun SavedThermometerBox(savedThermometer: SavedThermometer, modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
            .shadow(
                spacing.elevationShadowNormal,
                shape = RoundedCornerShape(spacing.spaceNormal)
            )
            .clip(RoundedCornerShape(spacing.radiusNormal))
            .background(MaterialTheme.colors.background)
    ) {
        Box(
            modifier = Modifier
                .padding(18.dp)
                .clip(RoundedCornerShape(spacing.radiusNormal))
                .background(Color.LightGray)
        ) {
            SavedThermometerValuesColumn(
                address = savedThermometer.address,
                name = savedThermometer.name,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun SavedThermometerValuesColumn(
    address: String,
    name: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(text = name)
        Text(
            text = address,
            fontSize = 38.sp,
            textAlign = TextAlign.Center,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 38.sp
        )
    }
}

@Preview(showBackground = true)
@Preview(name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SavedThermometerBoxPreview() {
    SavedThermometerBox(
        SavedThermometer(
            "address address address",
            "name"
        )
    )
}
