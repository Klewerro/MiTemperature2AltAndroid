package com.klewerro.mitemperature2alt.core.util

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

object LocalDateTimeUtils {
    fun Int.convertEpochSecondToLocalDateTimeUtc(): LocalDateTime {
        val instant = Instant.fromEpochSeconds(this.toLong())
        val localDateTime = instant.toLocalDateTime(TimeZone.UTC)

        localDateTime.toInstant(TimeZone.UTC).epochSeconds
        return localDateTime
    }

    fun LocalDateTime.toEpochSecondUtc(): Int = this.toInstant(TimeZone.UTC).epochSeconds.toInt()

    @OptIn(FormatStringsInDatetimeFormats::class)
    fun LocalDateTime.formatTest(): String = this.format(
        LocalDateTime.Format {
            byUnicodePattern("HH:mm, dd.MM.yy")
        }
    )
}
