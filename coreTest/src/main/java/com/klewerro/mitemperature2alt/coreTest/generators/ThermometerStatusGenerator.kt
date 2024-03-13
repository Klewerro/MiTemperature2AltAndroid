package com.klewerro.mitemperature2alt.coreTest.generators

import com.klewerro.mitemperature2alt.domain.model.ThermometerStatus

object ThermometerStatusGenerator {
    var thermometerStatus1 = ThermometerStatus(
        temperature = 10.0f,
        humidity = 50,
        voltage = 1.2f
    )
}
