package com.juansicardo.monitor.home

import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.juansicardo.monitor.R
import com.juansicardo.monitor.ble.BluetoothViewModel
import com.juansicardo.monitor.ble.LocationPermissionRequester
import com.juansicardo.monitor.constants.ApplicationConstants
import com.juansicardo.monitor.database.DataBaseViewModel
import com.juansicardo.monitor.dialog.LoadingDialogFragment
import com.juansicardo.monitor.profile.Profile
import com.juansicardo.monitor.settings.ProfileSettingsManager
import com.juansicardo.monitor.sms.SendSMSPermissionRequester
import java.nio.charset.StandardCharsets
import java.util.*

class HomeActivity : AppCompatActivity() {

    companion object {
        private const val PROFILE_ID_EXTRA_ID = "com.juansicardo.monitor.home_activity.profile_id"
        private const val MONITOR_SERVICE_UUID = "0000180c-0000-1000-8000-00805f9b34fb"
        private const val HEART_RATE_CHARACTERISTIC_UUID = "00002a56-0000-1000-8000-00805f9b34fb"
        private const val BLOOD_OXYGEN_CHARACTERISTIC_UUID = "00002a57-0000-1000-8000-00805f9b34fb"
        private const val BLOOD_PRESSURE_CHARACTERISTIC_UUID = "00002a58-0000-1000-8000-00805f9b34fb"
        private const val NOTIFICATIONS_DESCRIPTOR_UUID = "00002902-0000-1000-8000-00805f9b34fb"

        fun createIntent(context: Context, profileId: Int) = Intent(context, HomeActivity::class.java).apply {
            this.putExtra(PROFILE_ID_EXTRA_ID, profileId)
        }
    }

    //Declare UI elements
    private lateinit var loadingDialogFragment: LoadingDialogFragment

    //Initialize database
    private val dataBaseViewModel by lazy {
        ViewModelProvider(this).get(DataBaseViewModel::class.java)
    }

    //Elements read from database
    private lateinit var profileLiveData: LiveData<Profile>
    lateinit var profile: Profile

    //Permission management
    private var sendSMSPermissionRequester: SendSMSPermissionRequester =
        SendSMSPermissionRequester(this) { isPermissionGranted ->
            homeViewModel.setIsSendSMSPermissionGranted(isPermissionGranted)
        }
    private val locationPermissionRequester =
        LocationPermissionRequester(this) { isPermissionGranted ->
            homeViewModel.setIsLocationPermissionGranted(isPermissionGranted)
        }
    private var isLocationPermissionGranted = false

    //Business logic
    private val homeViewModel: HomeViewModel by viewModels()
    private val measurementViewModel: MeasurementViewModel by viewModels()

    //Bluetooth
    private val bluetoothViewModel: BluetoothViewModel by viewModels()

    //Adapter tells us if bluetooth is enabled
    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }
    private var isBluetoothEnabled = false

    //bleScanner lets us scan for bluetooth devices nearby
    private val bleScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }
    private var isScanning = false

    //scanFilter lets us find only those results that are relevant to us
    private val scanFilter = ScanFilter.Builder()
        .setDeviceName(ApplicationConstants.SMART_BAND_BLUETOOTH_DEVICE_NAME)
        .build()

    //scanSettings represents the settings used for scanning for bluetooth devices
    private val scanSettings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        .build()

    //bluetoothDevice represents the device we are connecting to
    private val mutableBluetoothDevice = MutableLiveData<BluetoothDevice>()
    private lateinit var bluetoothDevice: BluetoothDevice

    //scanCallback methods execute during the scanning phase
    private val scanCallback = object : ScanCallback() {

        //When getting a scan result
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            mutableBluetoothDevice.value = result.device
        }
    }

    //Tell when the smart band is actually connected to the phone
    private val mutableIsSmartBandConnected = MutableLiveData<Boolean>()
    private val isSmartBandConnected: LiveData<Boolean>
        get() = mutableIsSmartBandConnected

    //Represents our connection to the smart band
    private var bluetoothGatt: BluetoothGatt? = null

    //Needed to read and manipulate smart band information channels
    private lateinit var monitorService: BluetoothGattService
    private lateinit var heartRateCharacteristic: BluetoothGattCharacteristic
    private lateinit var heartRateNotificationsDescriptor: BluetoothGattDescriptor
    private lateinit var bloodOxygenCharacteristic: BluetoothGattCharacteristic
    private lateinit var bloodOxygenNotificationsDescriptor: BluetoothGattDescriptor

    //The methods of gattCallback are called during the connection phase
    private val gattCallback = object : BluetoothGattCallback() {

        //Run when connection to smart band changes
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //There is a bluetooth Gatt
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    //Smart band is connected
                    bluetoothGatt = gatt
                    homeViewModel.bluetoothGatt = gatt
                    Handler(Looper.getMainLooper()).post {
                        bluetoothGatt?.discoverServices()
                    }
                    mutableIsSmartBandConnected.postValue(true)

                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    //Smart band is disconnected
                    mutableIsSmartBandConnected.postValue(false)
                    gatt?.close()
                }

            } else {
                //There isn't a bluetooth Gatt
                mutableIsSmartBandConnected.postValue(false)
                gatt?.close()
            }
        }

        //Run when finding services
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {

            //Getting all the bluetooth information channels
            monitorService = gatt.getService(UUID.fromString(MONITOR_SERVICE_UUID))
            heartRateCharacteristic =
                monitorService.getCharacteristic(UUID.fromString(HEART_RATE_CHARACTERISTIC_UUID))
            heartRateNotificationsDescriptor =
                heartRateCharacteristic.getDescriptor(UUID.fromString(NOTIFICATIONS_DESCRIPTOR_UUID))
            bloodOxygenCharacteristic =
                monitorService.getCharacteristic(UUID.fromString(BLOOD_OXYGEN_CHARACTERISTIC_UUID))
            bloodOxygenNotificationsDescriptor =
                bloodOxygenCharacteristic.getDescriptor(UUID.fromString(NOTIFICATIONS_DESCRIPTOR_UUID))

            enableNotifications(heartRateCharacteristic)
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            if (characteristic == heartRateCharacteristic)
                homeViewModel.setHeartRate(characteristic.value.toLiteralString().toDouble().toInt())
            else if (characteristic == bloodOxygenCharacteristic)
                homeViewModel.setBloodOxygen(characteristic.value.toLiteralString().toDouble().toInt())
        }


        fun writeDescriptor(descriptor: BluetoothGattDescriptor, payload: ByteArray) {
            bluetoothGatt?.let { gatt ->
                descriptor.value = payload
                gatt.writeDescriptor(descriptor)
            } ?: error("Not connected to a BLE device!")
        }

        fun enableNotifications(characteristic: BluetoothGattCharacteristic) {
            val notificationDescriptorUuid = UUID.fromString(NOTIFICATIONS_DESCRIPTOR_UUID)

            val payload = when {
                characteristic.isIndicatable() -> BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
                characteristic.isNotifiable() -> BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                else -> return
            }

            characteristic.getDescriptor(notificationDescriptorUuid)?.let { notificationDescriptor ->
                if (bluetoothGatt?.setCharacteristicNotification(characteristic, true) == false)
                    return

                writeDescriptor(notificationDescriptor, payload)
            } ?: Log.e(ApplicationConstants.APP_TAG, "${characteristic.uuid} doesn't contain the CCC descriptor!")
        }

        override fun onDescriptorWrite(gatt: BluetoothGatt?, descriptor: BluetoothGattDescriptor?, status: Int) {
            super.onDescriptorWrite(gatt, descriptor, status)

            if (descriptor == heartRateNotificationsDescriptor)
                enableNotifications(bloodOxygenCharacteristic)
        }

        fun ByteArray.toLiteralString(): String {
            return String(this, StandardCharsets.UTF_8)
        }

        private fun BluetoothGattCharacteristic.isIndicatable(): Boolean =
            containsProperty(BluetoothGattCharacteristic.PROPERTY_INDICATE)

        private fun BluetoothGattCharacteristic.isNotifiable(): Boolean =
            containsProperty(BluetoothGattCharacteristic.PROPERTY_NOTIFY)

        private fun BluetoothGattCharacteristic.containsProperty(property: Int): Boolean {
            return properties and property != 0
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        Log.d(ApplicationConstants.APP_TAG, "Home Activity on Create")

        loadingDialogFragment = LoadingDialogFragment(this)
        homeViewModel.loadingDialogFragment = loadingDialogFragment
        loadingDialogFragment.show()

        //Intent variables
        val profileId = intent.getIntExtra(PROFILE_ID_EXTRA_ID, 0)

        //Initialize UI elements
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val navController = findNavController(R.id.fragment)
        bottomNavigationView.setupWithNavController(navController)

        mutableIsSmartBandConnected.value = false

        //Know if bluetooth is enabled, if not, change the UI so the user can enable it.
        bluetoothViewModel.bluetoothAdapter = bluetoothAdapter
        bluetoothViewModel.startBroadcast()

        bluetoothViewModel.isBluetoothEnabled.observe(this) { isBluetoothEnabled ->
            //Transmit to children fragments
            homeViewModel.setIsBluetoothEnabled(isBluetoothEnabled)

            this.isBluetoothEnabled = isBluetoothEnabled

            if (!isBluetoothEnabled)
                mutableIsSmartBandConnected.value = false

            startBleScan()
        }

        homeViewModel.isLocationPermissionGranted.observe(this) { isLocationPermissionGranted ->
            this.isLocationPermissionGranted = isLocationPermissionGranted
            startBleScan()
        }

        //Found a device during the scan phase
        mutableBluetoothDevice.observe(this) { bluetoothDevice ->
            //Stop scanning so the device is found one time only
            stopBleScan()
            this.bluetoothDevice = bluetoothDevice

            //Attempt connection to device
            this.bluetoothDevice.connectGatt(this, false, gattCallback)
        }

        isSmartBandConnected.observe(this) { isSmartBandConnected ->
            homeViewModel.setIsSmartBandConnected(isSmartBandConnected)

            if (isSmartBandConnected) {
                loadingDialogFragment.show()

                homeViewModel.bloodOxygen.observe(this) { bloodOxygen ->
                    loadingDialogFragment.dismiss()
                    measurementViewModel.recordBloodOxygenMeasurement(bloodOxygen)
                }

                homeViewModel.heartRate.observe(this) { heartRate ->
                    loadingDialogFragment.dismiss()
                    measurementViewModel.recordHeartRateMeasurement(heartRate)
                }

            } else {

                homeViewModel.bloodOxygen.removeObservers(this)
                homeViewModel.restartBloodOxygen()

                homeViewModel.heartRate.removeObservers(this)
                homeViewModel.restartHeartRate()

                startBleScan()
            }
        }

        //Get from database
        profileLiveData = dataBaseViewModel.findProfileById(profileId)
        profileLiveData.observe(this) { profile ->
            try {
                this.profile = profile
                homeViewModel.setProfile(profile)
                measurementViewModel.profileId = profile.profileId

                //Settings
                homeViewModel.setProfileSettingsManager(ProfileSettingsManager(this, profile.profileId))

                //Permission management
                sendSMSPermissionRequester.request()
                locationPermissionRequester.request()

                loadingDialogFragment.dismiss()
            } catch (e: Exception) {
                Log.d(ApplicationConstants.APP_TAG, "Se ejecuta")
                profileLiveData.removeObservers(this)
                bluetoothGatt?.close()
                finish()
            }
        }

        measurementViewModel.databaseViewModel = dataBaseViewModel
    }

    //Start scanning for the smart band
    private fun startBleScan() {
        if (isLocationPermissionGranted && isBluetoothEnabled && !isScanning) {
            isScanning = true
            bleScanner.startScan(listOf(scanFilter), scanSettings, scanCallback)
        }
    }

    //Stop scanning for smart band, usually called when already found it
    private fun stopBleScan() {
        if (isScanning) {
            isScanning = false
            bleScanner.stopScan(scanCallback)
        }
    }
}