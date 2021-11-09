package com.juansicardo.monitor.home

import androidx.lifecycle.ViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.juansicardo.monitor.constants.ApplicationConstants
import com.juansicardo.monitor.database.DataBaseViewModel
import com.juansicardo.monitor.measurement.Measurement

class MeasurementViewModel : ViewModel() {

    //Needs to be set manually
    var profileId: Int = 0
    lateinit var databaseViewModel: DataBaseViewModel

    fun recordHeartRateMeasurement(heartRateMeasurementValue: Int) {
        val measurement = Measurement(
            measurementId = 0,
            measurementType = ApplicationConstants.HEART_RATE_MEASUREMENT_TYPE_ID,
            value = heartRateMeasurementValue,
            date = MaterialDatePicker.todayInUtcMilliseconds(),
            profileOwnerId = profileId
        )

        databaseViewModel.insertMeasurement(measurement)
    }

    fun recordBloodOxygenMeasurement(bloodOxygenMeasurementValue: Int) {

    }
}