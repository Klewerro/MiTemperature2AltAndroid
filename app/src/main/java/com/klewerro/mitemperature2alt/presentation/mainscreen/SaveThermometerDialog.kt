package com.klewerro.mitemperature2alt.presentation.mainscreen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.window.Dialog
import com.klewerro.mitemperature2alt.R
import com.klewerro.mitemperature2alt.ui.LocalSpacing
import com.klewerro.mitemperature2alt.ui.theme.MiTemperature2AltTheme

@Composable
fun SaveThermometerDialog(isVisible: Boolean, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    val spacing = LocalSpacing.current

    // Focus
    val focusRequester = remember {
        FocusRequester()
    }
    var textFieldLoaded by remember {
        mutableStateOf(false)
    }

    // Text
    var textState by rememberSaveable {
        mutableStateOf("")
    }
    var cursorPosition by rememberSaveable {
        mutableIntStateOf(0)
    }
    val textFieldState by remember {
        derivedStateOf {
            TextFieldValue(
                text = textState,
                selection = TextRange(cursorPosition)
            )
        }
    }

    // Error
    var isAnyCharacterWritten by rememberSaveable {
        mutableStateOf(false)
    }
    val errorAlpha by animateFloatAsState(
        targetValue = if (isAnyCharacterWritten && textFieldState.text.isEmpty()) 1.0f else 0.0f,
        label = "Animate error alpha visibility"
    )

    if (isVisible) {
        Dialog(
            onDismissRequest = {
                onDismiss()
                textState = ""
                textFieldLoaded = false
            }
        ) {
            Card {
                Column(modifier = Modifier.padding(spacing.spaceScreen)) {
                    Text(
                        text = "Set thermometer name",
                        style = MaterialTheme.typography.h3
                    )
                    TextField(
                        value = textFieldState,
                        label = {
                            Text(text = stringResource(R.string.name))
                        },
                        onValueChange = {
                            textState = it.text
                            cursorPosition = it.selection.max
                            isAnyCharacterWritten = true
                        },
                        isError = isAnyCharacterWritten && textFieldState.text.isEmpty(),
                        trailingIcon = {
                            if (isAnyCharacterWritten && textFieldState.text.isEmpty()) {
                                Icon(
                                    imageVector = Icons.Filled.Error,
                                    contentDescription = stringResource(
                                        R.string.content_description_error_indicator
                                    ),
                                    tint = MaterialTheme.colors.error
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            isAnyCharacterWritten = true
                            if (textFieldState.text.isNotEmpty()) {
                                onSave(textState)
                            }
                        }),
                        modifier = Modifier
                            .padding(top = spacing.spaceNormal)
                            .focusRequester(focusRequester)
                            .onGloballyPositioned {
                                if (!textFieldLoaded) {
                                    focusRequester.requestFocus()
                                    textFieldLoaded = true
                                }
                            }
                    )
                    Text(
                        text = stringResource(R.string.thermometer_name_must_not_be_empty),
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.error,
                        modifier = Modifier
                            .alpha(errorAlpha)
                            .padding(
                                top = spacing.spaceExtraSmall,
                                bottom = spacing.spaceSmall
                            )
                    )
                    Text(
                        text = stringResource(R.string.save),
                        color = MaterialTheme.colors.primary,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .clickable {
                                isAnyCharacterWritten = true
                                if (textFieldState.text.isNotEmpty()) {
                                    onSave(textState)
                                }
                            }
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
fun ThermometerNameDialogPreview() {
    MiTemperature2AltTheme {
        SaveThermometerDialog(
            isVisible = true,
            onDismiss = { },
            onSave = { }
        )
    }
}
