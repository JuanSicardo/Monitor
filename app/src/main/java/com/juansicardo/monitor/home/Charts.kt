package com.juansicardo.monitor.home

import com.github.mikephil.charting.charts.ScatterChart

object Charts {

    fun configAsHeartRateChart(scatterChart: ScatterChart) {
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
            }

            with(axisRight) {
                axisMinimum = 0.0f
                axisMaximum = 200.0f
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
            }

            with(axisRight) {
                axisMinimum = 0.0f
                axisMaximum = 100.0f
            }
        }
    }
}