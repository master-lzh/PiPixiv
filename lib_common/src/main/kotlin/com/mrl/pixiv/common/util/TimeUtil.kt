package com.mrl.pixiv.common.util

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime

fun currentTimeMillis() = Clock.System.now().toEpochMilliseconds()


fun convertUtcStringToLocalDateTime(utcString: String): String {
    val currentTimeZone = TimeZone.currentSystemDefault()
    val instant = Instant.parse(utcString)
    val localDateTime = instant.toLocalDateTime(currentTimeZone)
    val formatter = LocalDateTime.Format {
        year()
        char('-')
        monthNumber()
        char('-')
        dayOfMonth()
        char(' ')
        hour()
        char(':')
        minute()
    }
    return localDateTime.format(formatter)
}