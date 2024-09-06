package com.klewerro.mitemperature2alt

import androidx.compose.ui.test.hasParent
import androidx.compose.ui.test.hasTestTag

object ProjectSemanticMatchers {
    val thermometerBox = hasParent(hasTestTag("thermometerBox"))
}
