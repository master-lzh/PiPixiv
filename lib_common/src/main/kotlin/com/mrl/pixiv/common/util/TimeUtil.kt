package com.mrl.pixiv.common.util

import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun convertUtcStringToLocalDateTime(utcString: String): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val zonedDateTime = ZonedDateTime.parse(utcString)
    val localDateTime = zonedDateTime.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime()
    return localDateTime.format(formatter)
}