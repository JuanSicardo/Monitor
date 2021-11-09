package com.juansicardo.monitor.ble

import android.bluetooth.BluetoothAdapter
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juansicardo.monitor.constants.ApplicationConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BluetoothViewModel : ViewModel() {

    companion object {
        private const val BROADCAST_PERIOD_MILLIS: Long = 500
    }

    lateinit var bluetoothAdapter: BluetoothAdapter

    //Broadcast bluetooth state
    //isBluetooth enable is observable and changes every BROADCAST_PERIOD_MILLIS milliseconds
    private val mutableIsBluetoothEnabled = MutableLiveData<Boolean>()
    val isBluetoothEnabled: LiveData<Boolean>
        get() = mutableIsBluetoothEnabled

    private var isBroadcasting = true

    private suspend fun updateIsBluetoothEnabled() {
        var lastValue = bluetoothAdapter.isEnabled
        mutableIsBluetoothEnabled.postValue(lastValue)

        while (isBroadcasting) {
            delay(BROADCAST_PERIOD_MILLIS)
            val currentValue = bluetoothAdapter.isEnabled

            //Only post value if changed
            if (lastValue != currentValue) {
                mutableIsBluetoothEnabled.postValue(currentValue)
                lastValue = currentValue
            }
        }
    }

    fun startBroadcast() {
        isBroadcasting = true
        viewModelScope.launch(Dispatchers.IO) {
            updateIsBluetoothEnabled()
        }
    }

    fun stopBroadcast() {
        isBroadcasting = false
    }
}