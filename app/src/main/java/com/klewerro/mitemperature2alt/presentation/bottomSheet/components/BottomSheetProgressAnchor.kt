package com.klewerro.mitemperature2alt.presentation.bottomSheet.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import com.klewerro.mitemperature2alt.coreUi.LocalSpacing

@Composable
fun BottomSheetProgressAnchor(isInProgress: Boolean, modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current

    AnimatedContent(
        targetState = isInProgress,
        transitionSpec = {
            slideInVertically(
                initialOffsetY = { it },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy
                )
            ).togetherWith(
                fadeOut()
            )
        },
        modifier = modifier,
        label = "Transition from bottom bar to progress bar."
    ) {
        if (it) {
            LinearProgressIndicator(
                strokeCap = StrokeCap.Round,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(spacing.spaceSmall)

            )
        } else {
            BottomSheetAnchor()
        }
    }
}
