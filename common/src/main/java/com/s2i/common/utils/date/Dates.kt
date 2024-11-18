package com.s2i.common.utils.date

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

object Dates {

    private fun iso8601Format(): SimpleDateFormat {
        return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
    }

    fun parseIso8601(dateString: String): Long {
        return try {
            iso8601Format().parse(dateString)?.time ?: 0
        } catch (e: ParseException) {
            0
        }
    }

    fun formatTimeDifference(startTime: Long, endTime: Long): String {
        val duration = endTime - startTime

        val seconds = TimeUnit.MILLISECONDS.toSeconds(duration)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(duration)
        val hours = TimeUnit.MILLISECONDS.toHours(duration)
        val days = TimeUnit.MILLISECONDS.toDays(duration)

        return when {
            seconds < 1 -> "Baru Saja"
            seconds < 60 -> "$seconds detik yang lalu"
            minutes < 60 -> "$minutes menit yang lalu"
            hours < 24 -> "$hours jam yang lalu"
            days < 7 -> "$days hari yang lalu"
            days < 30 -> "${days / 7} minggu yang lalu"
            days < 365 -> "${days / 30} bulan yang lalu"
            else -> SimpleDateFormat("d MMMM yyyy", Locale.getDefault()).format(Date(startTime))
        }
    }

}