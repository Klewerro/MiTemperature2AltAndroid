package com.klewerro.mitemperature2alt.coreUi.previewModel

import com.klewerro.mitemperature2alt.domain.model.RssiStrength
import com.klewerro.mitemperature2alt.domain.model.Thermometer
import com.klewerro.mitemperature2alt.domain.model.ThermometerConnectionStatus

object ThermometerPreviewModels {
    val thermometer = Thermometer(
        "Test thermometer 1",
        "00:00:00:00:00",
        21.1f,
        59,
        1.23f,
        RssiStrength.GOOD,
        ThermometerConnectionStatus.CONNECTED
    )
}
