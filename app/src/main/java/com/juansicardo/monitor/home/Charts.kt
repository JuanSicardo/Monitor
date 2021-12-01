package com.juansicardo.monitor.home

import android.content.Context
import android.graphics.Color
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.components.LimitLine
import com.juansicardo.monitor.R

object Charts {

    fun configAsHeartRateChart(scatterChart: ScatterChart, context: Context) {
        with(scatterChart) {
            setMaxVisibleValueCount(0)

            val limitLine = LimitLine(120f, context.getString(R.string.max_heart_rate)).apply {
                lineColor = Color.CYAN
                lineWidth = 1f
            }

            with(xAxis) {
                axisMinimum = 0.0f
                axisMaximum = 24.0f
                valueFormatter = TimeValueFormatter()
            }

            with(axisLeft) {
                axisMinimum = 0.0f
                axisMaximum = 200.0f
                valueFormatter = HeartRateValueFormatter(context)
                addLimitLine(limitLine)
            }

            with(axisRight) {
                axisMinimum = 0.0f
                axisMaximum = 200.0f
                valueFormatter = EmptyValueFormatter()
            }
        }
    }

    fun configAsBloodOxygenChart(scatterChart: ScatterChart, context: Context) {
        with(scatterChart) {
            setMaxVisibleValueCount(0)

            val limitLine = LimitLine(97f, context.getString(R.string.healthy_blood_oxygen)).apply {
                lineColor = Color.CYAN
                lineWidth = 1f
            }

            with(xAxis) {
                axisMinimum = 0.0f
                axisMaximum = 24.0f
                valueFormatter = TimeValueFormatter()
            }

            with(axisLeft) {
                axisMinimum = 86.0f
                axisMaximum = 100.0f
                valueFormatter = BloodOxygenValueFormatter()
                addLimitLine(limitLine)
            }

            with(axisRight) {
                axisMinimum = 86.0f
                axisMaximum = 100.0f
                valueFormatter = EmptyValueFormatter()
            }
        }
    }
}