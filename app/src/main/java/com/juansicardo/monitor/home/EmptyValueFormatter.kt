package com.juansicardo.monitor.home

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter

class EmptyValueFormatter: ValueFormatter() {

    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        return ""
    }
}