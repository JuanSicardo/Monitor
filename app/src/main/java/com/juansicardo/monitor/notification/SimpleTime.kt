package com.juansicardo.monitor.notification

import java.text.DecimalFormat

data class SimpleTime(
    val hourOfDay: Int,
    val minute: Int
) {
    override fun toString(): String {
        val hour12HourFormat = when (hourOfDay) {
            0 -> 12
            in 1..12 -> hourOfDay
            else -> hourOfDay - 12
        }

        val period = if (hourOfDay <= 0) "a.m." else "p.m."

        return "$hour12HourFormat:${DecimalFormat("00").format(minute)} $period"
    }
}
