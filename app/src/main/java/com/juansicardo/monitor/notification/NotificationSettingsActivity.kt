package com.juansicardo.monitor.notification

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial
import com.juansicardo.monitor.R
import com.juansicardo.monitor.settings.AppSettingsManager

class NotificationSettingsActivity : AppCompatActivity() {

    companion object {
        fun createIntent(context: Context) = Intent(context, NotificationSettingsActivity::class.java)
    }

    //Declare UI elements
    private lateinit var activateNotificationsSwitch: SwitchMaterial
    private lateinit var timeEditText: EditText
    private lateinit var timePickerFragment: TimePickerFragment

    //Settings
    private lateinit var appSettingsManager: AppSettingsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_settings)

        //Initialize UI elements
        activateNotificationsSwitch = findViewById(R.id.activate_notifications_switch)
        timeEditText = findViewById(R.id.time_edit_text)

        //Settings
        appSettingsManager = AppSettingsManager(this)

        //activateNotificationsSwitch configuration
        activateNotificationsSwitch.isChecked = getNotificationsEnabled()
        activateNotificationsSwitch.setOnClickListener {
            val switch = it as SwitchMaterial
            setNotificationsEnabled(switch.isChecked)
        }

        //timeEditText configuration
        timeEditText.setText(getNotificationTime().toString())
        timeEditText.setOnClickListener {
            timePickerFragment.show(supportFragmentManager, "timePicker")
        }

        //timePickerFragment configuration
        timePickerFragment = TimePickerFragment { simpleTime ->
            setNotificationTime(simpleTime)
        }
    }

    //Read/Write notifications activated setting from SharedPreferences
    private fun getNotificationsEnabled() = appSettingsManager.getAreNotificationsActivated()

    private fun setNotificationsEnabled(notificationsEnabled: Boolean) {
        //Initialize UI elements
        timeEditText.isEnabled = notificationsEnabled
        timeEditText.isClickable = notificationsEnabled

        appSettingsManager.setAreNotificationsActivated(notificationsEnabled)
    }

    //Read/Write notification time setting from SharedPreferences
    private fun getNotificationTime() = appSettingsManager.getNotificationTime()

    private fun setNotificationTime(simpleTime: SimpleTime) {
        //UI
        timeEditText.setText(simpleTime.toString())

        appSettingsManager.setNotificationTime(simpleTime)
    }
}