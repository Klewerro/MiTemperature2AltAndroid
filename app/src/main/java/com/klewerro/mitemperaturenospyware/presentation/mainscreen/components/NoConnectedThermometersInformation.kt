package com.klewerro.mitemperaturenospyware.presentation.mainscreen.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.klewerro.mitemperaturenospyware.R
import com.klewerro.mitemperaturenospyware.ui.LocalSpacing

@Composable
fun NoConnectedThermometersInformation(
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current

    Column(modifier = modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(spacing.spaceLarge))
        Text(
            text = stringResource(R.string.no_connected_thermometers),
            fontSize = 32.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(spacing.spaceNormal))
        Text(text = stringResource(R.string.no_connected_thermometers_rationale))
    }
}
