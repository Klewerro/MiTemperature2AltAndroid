package com.klewerro.mitemperature2alt.coreTest.generators

import com.klewerro.mitemperature2alt.domain.model.RssiStrength
import com.klewerro.mitemperature2alt.domain.model.Thermometer
import com.klewerro.mitemperature2alt.domain.model.ThermometerConnectionStatus

object ThermometerGenerator {
    var thermometer1 = Thermometer(
        "Test thermometer 1",
        "00:11:11:11:11:11",
        21.0f,
        50,
        3.00f,
        RssiStrength.VERY_GOOD,
        ThermometerConnectionStatus.DISCONNECTED
    )
}
