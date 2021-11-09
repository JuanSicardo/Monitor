package com.juansicardo.monitor.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.juansicardo.monitor.dialog.LoadingDialogFragment
import com.juansicardo.monitor.profile.Profile
import com.juansicardo.monitor.settings.ProfileSettingsManager

class HomeViewModel : ViewModel() {

    lateinit var loadingDialogFragment: LoadingDialogFragment

    private val mutableProfile = MutableLiveData<Profile>()
    val profile: LiveData<Profile>
        get() = mutableProfile

    fun setProfile(profile: Profile) {
        mutableProfile.value = profile
    }

    private val mutableProfileSettingsManager = MutableLiveData<ProfileSettingsManager>()
    val profileSettingsManager: LiveData<ProfileSettingsManager>
        get() = mutableProfileSettingsManager

    fun setProfileSettingsManager(profileSettingsManager: ProfileSettingsManager) {
        mutableProfileSettingsManager.value = profileSettingsManager
    }

    private val mutableIsSendSMSPermissionGranted = MutableLiveData<Boolean>()
    val isSendSMSPermissionGranted: LiveData<Boolean>
        get() = mutableIsSendSMSPermissionGranted

    fun setIsSendSMSPermissionGranted(isSendSMSPermissionGranted: Boolean) {
        mutableIsSendSMSPermissionGranted.value = isSendSMSPermissionGranted
    }

    private val mutableIsBluetoothEnabled = MutableLiveData<Boolean>()
    val isBluetoothEnabled: LiveData<Boolean>
        get() = mutableIsBluetoothEnabled

    fun setIsBluetoothEnabled(isBluetoothEnabled: Boolean) {
        mutableIsBluetoothEnabled.value = isBluetoothEnabled
    }

    private val mutableIsLocationPermissionGranted = MutableLiveData<Boolean>()
    val isLocationPermissionGranted: LiveData<Boolean>
        get() = mutableIsLocationPermissionGranted

    fun setIsLocationPermissionGranted(isLocationPermissionGranted: Boolean) {
        mutableIsLocationPermissionGranted.value = isLocationPermissionGranted
    }

    private val mutableIsSmartBandConnected = MutableLiveData<Boolean>()
    val isSmartBandConnected: LiveData<Boolean>
        get() = mutableIsSmartBandConnected

    fun setIsSmartBandConnected(isSmartBandConnected: Boolean) {
        mutableIsSmartBandConnected.value = isSmartBandConnected
    }

    private var mutableHeartRate = MutableLiveData<Int>()
    val heartRate: LiveData<Int>
        get() = mutableHeartRate

    fun setHeartRate(heartRate: Int) {
        mutableHeartRate.postValue(heartRate)
    }

    fun restartHeartRate() {
        mutableHeartRate = MutableLiveData<Int>()
    }

    private var mutableBloodOxygen = MutableLiveData<Int>()
    val bloodOxygen: LiveData<Int>
        get() = mutableBloodOxygen

    fun setBloodOxygen(bloodOxygen: Int) {
        mutableBloodOxygen.postValue(bloodOxygen)
    }

    fun restartBloodOxygen() {
        mutableBloodOxygen = MutableLiveData<Int>()
    }
}