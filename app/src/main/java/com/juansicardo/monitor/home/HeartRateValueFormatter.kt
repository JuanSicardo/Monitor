package com.juansicardo.monitor.home

import android.content.Context
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter
import com.juansicardo.monitor.R

class HeartRateValueFormatter(private val context: Context) : ValueFormatter() {

    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        return value.toInt().toString() + " " + context.getString(R.string.bps)
    }
}