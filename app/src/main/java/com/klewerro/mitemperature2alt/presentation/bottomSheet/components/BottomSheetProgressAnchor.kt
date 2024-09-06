package com.klewerro.mitemperature2alt.presentation.bottomSheet.components

import androidx.annotation.FloatRange
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

sealed class BottomSheetProgressType {
    data object Indeterminate : BottomSheetProgressType()
    data class Determinate(@FloatRange(from = 0.0, to = 1.0) val percent: Float) :
        BottomSheetProgressType() {
        constructor(value1: Int, value2: Int) : this(calculatePercent(value1, value2))

        private companion object {
            fun calculatePercent(value1: Int, value2: Int): Float = try {
                (value1.toFloat() / value2.toFloat())
            } catch (illegalArg: IllegalArgumentException) {
                0.0f
            }
        }
    }
}

@Composable
fun BottomSheetProgressAnchor(
    isInProgress: Boolean,
    progressType: BottomSheetProgressType,
    modifier: Modifier = Modifier
) {
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
            when (progressType) {
                is BottomSheetProgressType.Determinate -> {
                    LinearProgressIndicator(
                        strokeCap = StrokeCap.Round,
                        progress = progressType.percent,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(spacing.spaceSmall)

                    )
                }

                BottomSheetProgressType.Indeterminate -> {
                    LinearProgressIndicator(
                        strokeCap = StrokeCap.Round,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(spacing.spaceSmall)

                    )
                }
            }
        } else {
            BottomSheetAnchor()
        }
    }
}
