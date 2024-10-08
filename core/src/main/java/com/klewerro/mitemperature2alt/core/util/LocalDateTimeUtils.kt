package com.klewerro.mitemperature2alt.core.util

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

object LocalDateTimeUtils {
    fun getCurrentUtcTime() = Clock.System.now().toLocalDateTime(TimeZone.UTC)

    fun Int.convertEpochSecondToLocalDateTimeUtc(): LocalDateTime {
        val instant = Instant.fromEpochSeconds(this.toLong())
        val localDateTime = instant.toLocalDateTime(TimeZone.UTC)

        return localDateTime
    }

    fun Long.convertEpochMillisToLocalDate(): LocalDate {
        val instant = Instant.fromEpochMilliseconds(this)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        return localDateTime.date
    }

    fun LocalDateTime.toEpochSecondUtc(): Int = this.toInstant(TimeZone.UTC).epochSeconds.toInt()

    @OptIn(FormatStringsInDatetimeFormats::class)
    fun LocalDateTime.formatToFullHourDate(shortenedYear: Boolean = true): String = this.format(
        LocalDateTime.Format {
            byUnicodePattern(
                if (shortenedYear) "HH:mm, dd.MM.yy" else "HH:mm, dd.MM.yyyy"
            )
        }
    )

    @OptIn(FormatStringsInDatetimeFormats::class)
    fun LocalDate.formatToSimpleDate(shortenedYear: Boolean = true): String = this.format(
        LocalDate.Format {
            byUnicodePattern(
                if (shortenedYear) "dd.MM.yy" else "dd.MM.yyyy"
            )
        }
    )
}
