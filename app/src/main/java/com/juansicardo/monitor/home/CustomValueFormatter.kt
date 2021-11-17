package com.juansicardo.monitor.home

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter

class CustomValueFormatter : ValueFormatter() {

    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        val hour = getHour(value)
        val period = getPeriod(value)
        return "$hour$period"
    }

    private fun getHour(value: Float): String {
        with(value.toInt()) {
            return when (this) {
                0 -> 12.toString()
                in 1..12 -> this.toString()
                else -> (this - 12).toString()
            }
        }
    }

    private fun getMinute(value: Float): String {
        val totalMinutes = (value * 60).toInt()
        return (totalMinutes % 60).toString()
    }

    private fun getSeconds(value: Float): String {
        val secondsPerHour = 60 * 60
        val totalSeconds = (value * secondsPerHour).toInt()
        return (totalSeconds % secondsPerHour).toString()
    }

    private fun getPeriod(value: Float): String {
        return when (value.toInt()) {
            in 0..11 -> "am"
            in 12..23 -> "pm"
            else -> "am"
        }
    }
}