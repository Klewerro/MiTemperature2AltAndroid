package com.klewerro.mitemperature2alt.presentation.mainscreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.window.Dialog
import com.klewerro.mitemperature2alt.ui.LocalSpacing
import com.klewerro.mitemperature2alt.ui.theme.MiTemperature2AltTheme

@Composable
fun ThermometerNameDialog(
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    onTextValueChange: (String) -> Unit
) {
    val spacing = LocalSpacing.current
    var textState by remember {
        mutableStateOf("")
    }
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card {
            Column(modifier = Modifier.padding(spacing.spaceScreen)) {
                Text(
                    text = "Set thermometer name",
                    style = MaterialTheme.typography.h3
                )
                TextField(
                    value = textState,
                    onValueChange = {
                        textState = it
                        onTextValueChange(it)
                    },
                    label = {
                        Text(text = "Name")
                    },
                    modifier = Modifier.padding(vertical = spacing.spaceNormal)
                )
                Text(
                    text = "Save",
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .clickable {
                            onSave()
                        }
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
fun ThermometerNameDialogPreview() {
    MiTemperature2AltTheme {
        ThermometerNameDialog(onDismiss = { }, onSave = { }) {
        }
    }
}
