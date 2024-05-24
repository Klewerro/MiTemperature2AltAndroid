package com.klewerro.mitemperature2alt.addThermometerPresentation.search.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.klewerro.mitemperature2alt.coreUi.LocalSpacing
import com.klewerro.mitemperature2alt.coreUi.util.isAndroid12OrGreater

@Composable
fun PermissionDeclinedRationale(
    rationaleAndroid12Text: String,
    rationalePreAndroid12Text: String,
    buttonText: String,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            color = MaterialTheme.colors.onPrimary,
            text = if (isAndroid12OrGreater()) {
                rationaleAndroid12Text
            } else {
                rationalePreAndroid12Text
            }
        )
        Spacer(modifier = Modifier.height(spacing.spaceNormal))
        Button(
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.onPrimary),
            onClick = onButtonClick
        ) {
            Text(text = buttonText)
        }
    }
}
