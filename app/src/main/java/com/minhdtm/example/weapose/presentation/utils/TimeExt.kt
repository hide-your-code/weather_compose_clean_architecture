package com.minhdtm.example.weapose.presentation.utils

import java.text.SimpleDateFormat
import java.util.*

fun Long.toDateTime(format: String, zoneId: Int? = null): String {
    val date = Date().apply {
        time = this@toDateTime
    }

    return try {
        SimpleDateFormat(format, Locale.ENGLISH).apply {
            timeZone = TimeZone.getDefault().apply { zoneId?.let { rawOffset = it } }
        }.format(date)
    } catch (e: Exception) {
        SimpleDateFormat(format, Locale.ENGLISH).apply {
            timeZone = TimeZone.getDefault().apply { zoneId?.let { rawOffset = it } }
        }.format(date)
    }
}
