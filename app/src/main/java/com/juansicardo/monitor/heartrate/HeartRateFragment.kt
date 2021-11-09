package com.juansicardo.monitor.heartrate

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.juansicardo.monitor.R
import com.juansicardo.monitor.home.HomeViewModel
import com.juansicardo.monitor.profile.Profile

class HeartRateFragment : Fragment() {

    companion object {
        private const val ENABLE_BLUETOOTH_REQUEST_CODE = 1
    }

    //Declare UI elements
    private lateinit var dataDisplay: LinearLayoutCompat
    private lateinit var dataTextView: TextView
    private lateinit var warningDisplay: LinearLayoutCompat
    private lateinit var warningTextView: TextView
    private lateinit var activateBluetoothButton: Button

    //Extract data from parent activity
    private val homeViewModel: HomeViewModel by activityViewModels()
    private lateinit var profile: Profile

    //Bluetooth
    private var isBluetoothEnabled = false
    private var isLocationPermissionGranted = false
    private var isSmartBandConnected = false

    //Business logic
    private var heartRate = 0
        set(value) {
            field = value
            dataTextView.text = value.toString()
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.heart_rate_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Initialize UI elements
        dataDisplay = view.findViewById(R.id.data_display)
        dataTextView = view.findViewById(R.id.data_text_view)
        warningDisplay = view.findViewById(R.id.warning_display)
        warningTextView = view.findViewById(R.id.warning_text_view)
        activateBluetoothButton = view.findViewById(R.id.activate_bluetooth_button)

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
                homeViewModel.loadingDialogFragment.show()
                homeViewModel.heartRate.observe(viewLifecycleOwner) { heartRate ->
                    this.heartRate = heartRate
                    homeViewModel.loadingDialogFragment.dismiss()
                }
            } else {
                homeViewModel.heartRate.removeObservers(viewLifecycleOwner)
                homeViewModel.restartHeartRate()
            }

            updateUI()
        }

        //Action listeners
        activateBluetoothButton.setOnClickListener {
            promptEnableBluetooth()
        }
    }

    //Setup the UI to show the state of bluetooth connection
    private fun updateUI() {
        //Bluetooth disabled
        if (!isBluetoothEnabled) {
            dataDisplay.visibility = View.GONE
            warningTextView.text = getString(R.string.bluetooth_disabled_warning)
            warningDisplay.visibility = View.VISIBLE
            activateBluetoothButton.visibility = View.VISIBLE
        } else if (!isLocationPermissionGranted) {
            dataDisplay.visibility = View.GONE
            warningTextView.text = getString(R.string.location_disabled_warning)
            warningDisplay.visibility = View.VISIBLE
            activateBluetoothButton.visibility = View.GONE
        } else if (!isSmartBandConnected) {
            dataDisplay.visibility = View.GONE
            warningTextView.text = getString(R.string.smart_band_not_connected_warning)
            warningDisplay.visibility = View.VISIBLE
            activateBluetoothButton.visibility = View.GONE
        } else {
            dataDisplay.visibility = View.VISIBLE
            warningDisplay.visibility = View.GONE
            activateBluetoothButton.visibility = View.GONE
        }
    }

    //Ask the user to activate bluetooth
    private fun promptEnableBluetooth() {
        if (!isBluetoothEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, ENABLE_BLUETOOTH_REQUEST_CODE)
        }
    }
}