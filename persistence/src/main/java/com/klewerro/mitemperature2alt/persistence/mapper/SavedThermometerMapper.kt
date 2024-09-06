package com.klewerro.mitemperature2alt.persistence.mapper

import com.klewerro.mitemperature2alt.domain.model.SavedThermometer
import com.klewerro.mitemperature2alt.persistence.entity.ThermometerEntity

fun ThermometerEntity.toSavedThermometer() = SavedThermometer(
    address = this.address,
    name = this.name
)

fun SavedThermometer.toThermometerEntity() = ThermometerEntity(
    address = this.address,
    name = this.name
)
