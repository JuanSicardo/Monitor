package com.juansicardo.monitor.home

import android.content.Context
import com.github.mikephil.charting.charts.ScatterChart

object Charts {

    fun configAsHeartRateChart(scatterChart: ScatterChart, context: Context) {
        with(scatterChart) {
            setMaxVisibleValueCount(0)

            with(xAxis) {
                axisMinimum = 0.0f
                axisMaximum = 24.0f
                valueFormatter = TimeValueFormatter()
            }

            with(axisLeft) {
                axisMinimum = 0.0f
                axisMaximum = 200.0f
                valueFormatter = HeartRateValueFormatter(context)
            }

            with(axisRight) {
                axisMinimum = 0.0f
                axisMaximum = 200.0f
                valueFormatter = EmptyValueFormatter()
            }
        }
    }

    fun configAsBloodOxygenChart(scatterChart: ScatterChart) {
        with(scatterChart) {
            setMaxVisibleValueCount(0)

            with(xAxis) {
                axisMinimum = 0.0f
                axisMaximum = 24.0f
                valueFormatter = TimeValueFormatter()
            }

            with(axisLeft) {
                axisMinimum = 0.0f
                axisMaximum = 100.0f
                valueFormatter = BloodOxygenValueFormatter()
            }

            with(axisRight) {
                axisMinimum = 0.0f
                axisMaximum = 100.0f
                valueFormatter = EmptyValueFormatter()
            }
        }
    }
}