package com.klewerro.mitemperaturenospyware.presentation.mainscreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.klewerro.mitemperaturenospyware.ui.LocalSpacing

@Composable
fun TopBar(onButtonClick: () -> Unit) {
    val spacing = LocalSpacing.current

    TopAppBar(
        title = { Text(text = "Mi Temperature No Spyware") },
        actions = {
            Icon(
                imageVector = Icons.Default.AddCircle,
                contentDescription = "Add new thermometer",
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(spacing.radiusCircle))
                    .clickable {
                        onButtonClick()
                    }
                    .padding(1.dp)

            )
        },
        backgroundColor = MaterialTheme.colors.primary
    )
}
