package com.klewerro.mitemperature2alt.coreUi.util

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.pointer.pointerInput

fun Modifier.clearFocusOnClick(focusManager: FocusManager): Modifier = this.pointerInput(Unit) {
    detectTapGestures(onTap = { focusManager.clearFocus() })
}

fun Modifier.alphaDisabled(isDisabled: Boolean) = this.alpha(
    if (isDisabled) .5f else 1f // ContentAlpha.disabled
)
