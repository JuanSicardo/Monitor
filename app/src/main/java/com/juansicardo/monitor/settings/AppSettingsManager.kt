package com.juansicardo.monitor.settings

import android.content.Context
import androidx.preference.PreferenceManager
import com.juansicardo.monitor.notification.SimpleTime
import java.util.*

class AppSettingsManager(context: Context) {

    companion object {
        private const val ARE_NOTIFICATIONS_ACTIVATED_KEY =
            "com.juansicardo.monitor.app_settings_manager.are_notifications_activated"
        private const val NOTIFICATION_HOUR_KEY =
            "com.juansicardo.monitor.app_settings_manager.notification_hour"
        private const val NOTIFICATION_MINUTE_KEY =
            "com.juansicardo.monitor.app_settings_manager.notification_minute"
    }

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    //Setting that tells you if the notifications system is active or not
    fun getAreNotificationsActivated(): Boolean {
        return sharedPreferences.getBoolean(ARE_NOTIFICATIONS_ACTIVATED_KEY, false)
    }

    fun setAreNotificationsActivated(boolean: Boolean) {
        sharedPreferences.edit().apply {
            this.putBoolean(ARE_NOTIFICATIONS_ACTIVATED_KEY, boolean)
            this.apply()
        }
    }

    //Setting that tells you at what time of the day the notification is going to trigger
    fun getNotificationTime(): SimpleTime {
        if (!sharedPreferences.contains(NOTIFICATION_HOUR_KEY) || !sharedPreferences.contains(NOTIFICATION_MINUTE_KEY)) {
            with(Calendar.getInstance()) {
                setNotificationTime(SimpleTime(get(Calendar.HOUR_OF_DAY), get(Calendar.MINUTE)))
            }
        }
        val hourOfDay = sharedPreferences.getInt(NOTIFICATION_HOUR_KEY, 0)
        val minute = sharedPreferences.getInt(NOTIFICATION_MINUTE_KEY, 0)
        return SimpleTime(hourOfDay, minute)
    }

    fun setNotificationTime(simpleTime: SimpleTime) {
        sharedPreferences.edit().apply {
            this.putInt(NOTIFICATION_HOUR_KEY, simpleTime.hourOfDay)
            this.putInt(NOTIFICATION_MINUTE_KEY, simpleTime.minute)
            this.apply()
        }
    }
}