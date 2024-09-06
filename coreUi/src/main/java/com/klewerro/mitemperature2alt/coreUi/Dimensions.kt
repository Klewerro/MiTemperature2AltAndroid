package com.klewerro.mitemperature2alt.coreUi

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Dimensions(
    val zero: Dp = 0.dp,
    val spaceExtraSmall: Dp = 4.dp,
    val spaceSmall: Dp = 8.dp,
    val spaceNormal: Dp = 16.dp,
    val spaceScreen: Dp = 16.dp,
    val spaceLarge: Dp = 32.dp,
    val spaceExtraLarge: Dp = 64.dp,
    val radiusSmall: Dp = 8.dp,
    val radiusNormal: Dp = 16.dp,
    val radiusCircle: Dp = 90.dp,
    val elevationShadowNormal: Dp = 4.dp
)

val LocalSpacing = compositionLocalOf {
    Dimensions()
}
