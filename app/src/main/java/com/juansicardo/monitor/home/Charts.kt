package com.juansicardo.monitor.home

import android.content.Context
import com.github.mikephil.charting.charts.ScatterChart

object Charts {

    fun configAsHeartRateChart(scatterChart: ScatterChart) {
        with(scatterChart) {
            setTouchEnabled(false)

            with(xAxis) {
                axisMinimum = 0.0f
                axisMaximum = 24.0f
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
}