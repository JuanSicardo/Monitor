package com.juansicardo.monitor.bloodoxygen

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.github.mikephil.charting.charts.ScatterChart
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import com.juansicardo.monitor.R
import com.juansicardo.monitor.home.Charts
import com.juansicardo.monitor.home.HistoryChart
import com.juansicardo.monitor.home.HistoryViewModel
import com.juansicardo.monitor.home.HomeViewModel
import com.juansicardo.monitor.profile.Profile
import java.text.DateFormat
import java.util.*

class BloodOxygenFragment : Fragment() {

    companion object {
        private const val ENABLE_BLUETOOTH_REQUEST_CODE = 1
        private const val DATE_PICKER_TAG = "com.juansicardo.monitor.blood_oxygen_fragment.date_picker"
    }

    //Declare UI elements
    private lateinit var dataDisplay: LinearLayoutCompat
    private lateinit var dataTextView: TextView
    private lateinit var warningDisplay: LinearLayoutCompat
    private lateinit var warningTextView: TextView
    private lateinit var activateBluetoothButton: Button
    private lateinit var dateInputLayout: TextInputLayout
    private lateinit var dateEditText: EditText
    private lateinit var measurementGraph: ScatterChart
    private lateinit var datePicker: MaterialDatePicker<Long>

    //Extract data from parent activity
    private val homeViewModel: HomeViewModel by activityViewModels()
    private lateinit var profile: Profile

    //Bluetooth
    private var isBluetoothEnabled = false
    private var isLocationPermissionGranted = false
    private var isSmartBandConnected = false

    //Business logic
    private var bloodOxygen = 0
        set(value) {
            field = value
            dataTextView.text = value.toString()
        }

    private var date = 0L
        set(value) {
            field = value
            bloodOxygenMeasurementHistory.setDate(field)
            bloodOxygenHistoryChart.updateData()

            val dateObject = Date(field + 86400000)
            val dateFormat = DateFormat.getDateInstance()
            val dateString = dateFormat.format(dateObject)
            dateEditText.setText(dateString)
        }

    private val bloodOxygenHistoryViewModel: HistoryViewModel by activityViewModels()
    private lateinit var bloodOxygenMeasurementHistory: HistoryViewModel.MeasurementHistory
    private lateinit var bloodOxygenHistoryChart: HistoryChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.blood_oxygen_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Initialize UI elements
        dataDisplay = view.findViewById(R.id.data_display)
        dataTextView = view.findViewById(R.id.data_text_view)
        warningDisplay = view.findViewById(R.id.warning_display)
        warningTextView = view.findViewById(R.id.warning_text_view)
        activateBluetoothButton = view.findViewById(R.id.activate_bluetooth_button)
        measurementGraph = view.findViewById(R.id.measurement_graph)
        dateInputLayout = view.findViewById(R.id.date_input_layout)
        dateEditText = view.findViewById(R.id.date_edit_text)

        val today = MaterialDatePicker.todayInUtcMilliseconds()
        val constraintBuilder = CalendarConstraints.Builder().setValidator(DateValidatorPointBackward.before(today))
        datePicker = MaterialDatePicker.Builder.datePicker().setTitleText("").setSelection(today)
            .setCalendarConstraints(constraintBuilder.build()).build()
        datePicker.addOnPositiveButtonClickListener { date ->
            this.date = date
        }

        //Get from parent activity
        homeViewModel.profile.observe(viewLifecycleOwner) { profile ->
            this.profile = profile
        }

        homeViewModel.isBluetoothEnabled.observe(viewLifecycleOwner) { isBluetoothEnabled ->
            this.isBluetoothEnabled = isBluetoothEnabled
            updateUI()
        }

        homeViewModel.isLocationPermissionGranted.observe(viewLifecycleOwner) { isLocationPermissionGranted ->
            this.isLocationPermissionGranted = isLocationPermissionGranted
            updateUI()
        }

        homeViewModel.isSmartBandConnected.observe(viewLifecycleOwner) { isSmartBandConnected ->
            this.isSmartBandConnected = isSmartBandConnected

            if (isSmartBandConnected) {
                homeViewModel.bloodOxygen.observe(viewLifecycleOwner) { bloodOxygen ->
                    this.bloodOxygen = bloodOxygen
                }
            }

            updateUI()
        }

        bloodOxygenHistoryViewModel.bloodOxygenMeasurementHistory.observe(viewLifecycleOwner) { bloodOxygenMeasurementHistory ->
            this.bloodOxygenMeasurementHistory = bloodOxygenMeasurementHistory
            Charts.configAsBloodOxygenChart(measurementGraph)
            bloodOxygenHistoryChart = HistoryChart(measurementGraph, listOf(bloodOxygenMeasurementHistory))
            date = MaterialDatePicker.todayInUtcMilliseconds()
        }

        //Action listeners
        activateBluetoothButton.setOnClickListener {
            promptEnableBluetooth()
        }

        dateEditText.setOnClickListener {
            datePicker.show(requireFragmentManager(), DATE_PICKER_TAG)
        }
    }

    //Setup the UI to show the state of bluetooth connection
    private fun updateUI() {
        //Bluetooth disabled
        if (!isBluetoothEnabled) {
            dataDisplay.visibility = View.GONE
            dataTextView.text = ""
            warningTextView.text = getString(R.string.bluetooth_disabled_warning)
            warningDisplay.visibility = View.VISIBLE
            activateBluetoothButton.visibility = View.VISIBLE
        } else if (!isLocationPermissionGranted) {
            dataDisplay.visibility = View.GONE
            dataTextView.text = ""
            warningTextView.text = getString(R.string.location_disabled_warning)
            warningDisplay.visibility = View.VISIBLE
            activateBluetoothButton.visibility = View.GONE
        } else if (!isSmartBandConnected) {
            dataDisplay.visibility = View.GONE
            dataTextView.text = ""
            warningTextView.text = getString(R.string.smart_band_not_connected_warning)
            warningDisplay.visibility = View.VISIBLE
            activateBluetoothButton.visibility = View.GONE
        } else {
            dataDisplay.visibility = View.VISIBLE
            warningDisplay.visibility = View.GONE
            activateBluetoothButton.visibility = View.GONE
        }
    }

    //Ask user to activate bluetooth
    private fun promptEnableBluetooth() {
        if (!isBluetoothEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, ENABLE_BLUETOOTH_REQUEST_CODE)
        }
    }
}