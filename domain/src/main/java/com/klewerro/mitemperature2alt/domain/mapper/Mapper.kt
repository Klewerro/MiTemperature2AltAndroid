package com.klewerro.mitemperature2alt.domain.mapper

interface Mapper<T, Y> {
    fun map(data: T): Y
}
