package com.juansicardo.monitor.settings

import android.content.Context
import androidx.preference.PreferenceManager

class ProfileSettingsManager(context: Context, profileId: Int) {

    companion object {
        private const val PROFILE_SETTINGS_KEYS_PREFIX =
            "com.juansicardo.monitor.profile_settings_manager."
        private const val ARE_EMERGENCY_SMS_ENABLED_KEY_SUFFIX =
            ".are_emergency_sms_enabled"
    }

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val areEmergencySMSEnabledKey =
        "$PROFILE_SETTINGS_KEYS_PREFIX$profileId$ARE_EMERGENCY_SMS_ENABLED_KEY_SUFFIX"

    fun getAreEmergencySMSEnabled(): Boolean {
        return sharedPreferences.getBoolean(areEmergencySMSEnabledKey, false)
    }

    fun setAreEmergencySMSEnabled(boolean: Boolean) {
        sharedPreferences.edit().apply {
            this.putBoolean(areEmergencySMSEnabledKey, boolean)
            this.apply()
        }
    }

    fun deleteAreEmergencySMSEnabled() {
        sharedPreferences.edit().apply {
            this.remove(areEmergencySMSEnabledKey)
            this.apply()
        }
    }

}