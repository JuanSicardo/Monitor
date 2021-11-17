package com.juansicardo.monitor.home

import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.data.ScatterData
import com.github.mikephil.charting.data.ScatterDataSet
import com.juansicardo.monitor.R

class HistoryChart(
        private val scatterChart: ScatterChart,
        private val measurementHistories: List<HistoryViewModel.MeasurementHistory>
) {

    private var scatterData = ScatterData()
    private val datasets = mutableListOf<ScatterDataSet>()

    init {
        for (i in measurementHistories.indices) {
            measurementHistories[i].setOnDataChangeListener { scatterDataSet ->
                datasets.add(i, scatterDataSet)
                updateData()
            }
        }
    }

    private fun updateData() {
        scatterData = ScatterData()
        setDatasetColors()
        datasets.forEach { scatterData.addDataSet(it) }
        scatterChart.data = scatterData
        scatterChart.invalidate()
    }

    private fun setDatasetColors() {
        for (i in 0 until datasets.size) {
            if (i % 2 == 0)
                datasets[i].color = R.color.purple_700
            else
                datasets[i].color = R.color.teal_700
        }
    }
}