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
    private val datasets = mutableMapOf<Int, ScatterDataSet>()

    init {

        try {
            for (i in measurementHistories.indices) {
                datasets[i] = measurementHistories[i].toDataSet()
            }
            updateData()
        } catch (exception: Exception) {
        }

        for (i in measurementHistories.indices) {
            measurementHistories[i].setOnDataChangeListener { scatterDataSet ->
                datasets[i] = scatterDataSet
                updateData()
            }
        }
    }

    fun updateData() {
        scatterData.clearValues()
        scatterChart.data = scatterData
        scatterChart.notifyDataSetChanged()
        scatterChart.invalidate()

        setDatasetColors()
        scatterData = ScatterData()
        datasets.values.forEach { scatterData.addDataSet(it) }
        scatterChart.data = scatterData
        scatterChart.notifyDataSetChanged()
        scatterChart.invalidate()
    }

    private fun setDatasetColors() {
        for (i in datasets.keys) {
            if (i % 2 == 0)
                datasets[i]?.color = R.color.purple_700
            else
                datasets[i]?.color = R.color.teal_700
        }
    }
}