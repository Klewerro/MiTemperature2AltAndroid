package com.klewerro.mitemperature2alt.domain.mapper

import com.klewerro.mitemperature2alt.domain.model.RssiStrength

object RssiStrengthMapper : Mapper<Int?, RssiStrength> {
    override fun map(data: Int?): RssiStrength {
        return data?.let { dataValue ->
            when {
                dataValue <= 30 -> RssiStrength.EXCELLENT
                dataValue <= 60 -> RssiStrength.VERY_GOOD
                dataValue <= 75 -> RssiStrength.GOOD
                dataValue < 85 -> RssiStrength.POOR
                else -> RssiStrength.UNUSABLE
            }
        } ?: run {
            RssiStrength.UNKNOWN
        }
    }
}
