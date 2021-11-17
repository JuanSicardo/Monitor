package com.juansicardo.monitor.home

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.ScatterDataSet
import com.google.android.material.datepicker.MaterialDatePicker
import com.juansicardo.monitor.constants.ApplicationConstants
import com.juansicardo.monitor.database.DataBaseViewModel
import com.juansicardo.monitor.measurement.Measurement

typealias OnDataChangeCallback = (ScatterDataSet) -> (Unit)

class HistoryViewModel : ViewModel() {

    private lateinit var lifecycleOwner: LifecycleOwner
    private lateinit var dataBaseViewModel: DataBaseViewModel
    private var profileId = -1
    private var hasBeenInitiated = false

    private val mutableHeartRateMeasurementHistory = MutableLiveData<MeasurementHistory>()
    val heartRateMeasurementHistory: LiveData<MeasurementHistory>
        get() = mutableHeartRateMeasurementHistory

    private val mutableBloodOxygenMeasurementHistory = MutableLiveData<MeasurementHistory>()
    val bloodOxygenMeasurementHistory: LiveData<MeasurementHistory>
        get() = mutableBloodOxygenMeasurementHistory

    //Because we don't have access to the builder method of this class, we use initialize for this purpose
    //lifecycleOwner: necessary to use LiveData.
    //dataBaseVideModel: used to access the database.
    //profileId: the id of the profile currently using the application.
    fun initialize(lifecycleOwner: LifecycleOwner, dataBaseViewModel: DataBaseViewModel, profileId: Int) {
        this.lifecycleOwner = lifecycleOwner
        this.dataBaseViewModel = dataBaseViewModel
        this.profileId = profileId
        hasBeenInitiated = true

        initializeMeasurementHistoryObjects()
    }

    private fun initializeMeasurementHistoryObjects() {
        mutableHeartRateMeasurementHistory.value =
            (MeasurementHistory(ApplicationConstants.HEART_RATE_MEASUREMENT_TYPE_ID))
        mutableBloodOxygenMeasurementHistory.value =
            (MeasurementHistory(ApplicationConstants.BLOOD_OXYGEN_MEASUREMENT_TYPE_ID))
    }

    //Represents the history of measurements of a specific date
    //type: it establishes if the measurements are of heart rate, blood oxygen, systolic blood pressure o diastolic blood pressure
    //dateTimestamp: a timestamp in milliseconds that represents an arbitrary time of the date you want to consult.
    inner class MeasurementHistory(
        private val type: Int,
        private var dateTimestamp: Long = System.currentTimeMillis() - 21600000
    ) {

        private lateinit var measurementsLiveData: LiveData<List<Measurement>>
        private lateinit var measurements: List<Measurement>
        private val start = dateTimestamp - (dateTimestamp % 86400000)
        private val end = dateTimestamp + 86400000 - 1

        private val onDataChangedCallbacks = mutableListOf<OnDataChangeCallback>()

        private val chartLabel = when (type) {
            ApplicationConstants.HEART_RATE_MEASUREMENT_TYPE_ID -> "Heart Rate"
            ApplicationConstants.BLOOD_OXYGEN_MEASUREMENT_TYPE_ID -> "Blood oxygen"
            ApplicationConstants.SYS_BLOOD_PRESSURE_MEASUREMENT_TYPE_ID -> "Systolic Blood Pressure"
            else -> "Diastolic Blood Pressure"
        }

        init {
            if (!hasBeenInitiated) throw Exception("HistoryViewMode hasn't been initialized")
            initializeLiveData()
        }

        private fun initializeLiveData() {
            measurementsLiveData = dataBaseViewModel.findMeasurementsByProfileTypeAndDate(profileId, type, start, end)
            measurementsLiveData.observe(lifecycleOwner) { measurements ->

                this.measurements = measurements

                onDataChangedCallbacks.forEach { onDataChangedCallback ->
                    onDataChangedCallback.invoke(toDataSet())
                }
            }
        }

        //Changes the LiveData currently being used.
        private fun updateLiveData() {
            measurementsLiveData.removeObservers(lifecycleOwner)
            initializeLiveData()
        }

        fun setDate(dateTimestamp: Long) {
            this.dateTimestamp = dateTimestamp
            updateLiveData()
        }

        private fun toDataSet(): ScatterDataSet {
            val entries = mutableListOf<Entry>()
            measurements.forEach { entries.add(it.toChartEntry()) }
            return ScatterDataSet(entries, chartLabel)
        }

        fun setOnDataChangeListener(callback: OnDataChangeCallback) {
            onDataChangedCallbacks.add(callback)
        }
    }
}