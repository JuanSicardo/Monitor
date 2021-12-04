package com.juansicardo.monitor.home

import android.util.Log
import androidx.lifecycle.ViewModel
import com.juansicardo.monitor.constants.ApplicationConstants
import com.juansicardo.monitor.database.DataBaseViewModel
import com.juansicardo.monitor.measurement.Measurement

class MeasurementViewModel : ViewModel() {

    //Needs to be set manually
    var profileId: Int = 0
    var maxHeartRate: Int = 0
    lateinit var databaseViewModel: DataBaseViewModel

    private val currentTimeStamp
        get() = System.currentTimeMillis()

    fun recordHeartRateMeasurement(heartRateMeasurementValue: Int) {
        val measurement = Measurement(
            measurementId = 0,
            measurementType = ApplicationConstants.HEART_RATE_MEASUREMENT_TYPE_ID,
            value = heartRateMeasurementValue,
            date = currentTimeStamp,
            profileOwnerId = profileId
        )

        databaseViewModel.insertMeasurement(measurement)
    }

    fun recordBloodOxygenMeasurement(bloodOxygenMeasurementValue: Int) {
        val measurement = Measurement(
            measurementId = 0,
            measurementType = ApplicationConstants.BLOOD_OXYGEN_MEASUREMENT_TYPE_ID,
            value = bloodOxygenMeasurementValue,
            date = currentTimeStamp,
            profileOwnerId = profileId
        )

        databaseViewModel.insertMeasurement(measurement)
    }
}