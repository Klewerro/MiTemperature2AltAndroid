package com.klewerro.mitemperature2alt.addThermometerPresentation.name

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import com.klewerro.mitemperature2alt.coreUi.LocalSpacing
import com.klewerro.mitemperature2alt.coreUi.R
import com.klewerro.mitemperature2alt.coreUi.components.ThermometerBox
import com.klewerro.mitemperature2alt.coreUi.theme.MiTemperature2AltTheme

@Composable
fun EnterNameThermometerBox(
    temperature: Float,
    humidity: Int,
    voltage: Float,
    text: String,
    onTextChange: (String) -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false
) {
    val spacing = LocalSpacing.current
    val keyboardController = LocalSoftwareKeyboardController.current

    ThermometerBox(
        temperature = temperature,
        humidity = humidity,
        voltage = voltage,
        modifier = modifier
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            label = { Text(text = stringResource(R.string.thermometer_name)) },
            maxLines = 2,
            isError = isError,
            modifier = Modifier
                .padding(horizontal = spacing.spaceSmall)
                .onFocusChanged {
                    if (!it.isFocused) {
                        keyboardController?.hide()
                    }
                },
            keyboardActions = KeyboardActions(
                onSend = {
                    onDone()
                    defaultKeyboardAction(ImeAction.Send)
                }
            ),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Send
            )

        )
    }
}

@Preview(name = "EnterNameThermometerBox name empty preview")
@Composable
private fun Preview1() {
    MiTemperature2AltTheme {
        EnterNameThermometerBox(
            21.1f,
            56,
            1.230f,
            "",
            {},
            {}
        )
    }
}

@Preview(name = "EnterNameThermometerBox name entered preview")
@Composable
private fun Preview2() {
    MiTemperature2AltTheme {
        EnterNameThermometerBox(
            21.1f,
            56,
            1.230f,
            "Name",
            {},
            {}
        )
    }
}
