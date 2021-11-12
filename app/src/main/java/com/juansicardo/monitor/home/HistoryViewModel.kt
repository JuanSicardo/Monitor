package com.juansicardo.monitor.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.juansicardo.monitor.database.DataBaseViewModel
import com.juansicardo.monitor.measurement.Measurement

class HistoryViewModel: ViewModel() {

    private lateinit var dataBaseViewModel: DataBaseViewModel
    private var profileId = -1
    private var type = -1
    private var hasBeenInitiated = false

    fun initialize(dataBaseViewModel: DataBaseViewModel, profileId: Int, type: Int) {
        this.dataBaseViewModel = dataBaseViewModel
        this.profileId = profileId
        hasBeenInitiated = true
    }

    fun getMeasurementsByDate(dateTimestamp: Long): LiveData<List<Measurement>> {
        if (!hasBeenInitiated) throw Exception("HistoryViewModel of type $type, hasn't been initialized")

        val start = dateTimestamp - (dateTimestamp % 86400000)
        val end = dateTimestamp + 86400000 - 1

        return dataBaseViewModel.findMeasurementsByProfileTypeAndDate(profileId, type, start, end)
    }
}