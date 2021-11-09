package com.juansicardo.monitor.sms

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class SendSMSPermissionRequester(private val activity: AppCompatActivity, private val callback: (Boolean) -> Unit) {

    private val requestPermissionLauncher =
        activity.registerForActivityResult(ActivityResultContracts.RequestPermission(), callback)

    fun request() {
        when {
            ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.SEND_SMS
            ) == PackageManager.PERMISSION_GRANTED -> {
                //Permission is granted
                callback.invoke(true)
            }

            ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.SEND_SMS) -> {
                //Additional rationale should be displayed
                requestPermissionLauncher.launch(Manifest.permission.SEND_SMS)
            }

            else -> {
                //Permission has not been asked yet
                requestPermissionLauncher.launch(Manifest.permission.SEND_SMS)
            }
        }
    }
}