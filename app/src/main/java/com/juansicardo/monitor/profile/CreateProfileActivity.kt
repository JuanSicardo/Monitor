package com.juansicardo.monitor.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import com.juansicardo.monitor.R
import com.juansicardo.monitor.constants.ApplicationConstants
import com.juansicardo.monitor.database.DataBaseViewModel
import com.juansicardo.monitor.dialog.LoadingDialogFragment
import com.juansicardo.monitor.emergencycontact.EmergencyContactsActivity
import com.juansicardo.monitor.settings.ProfileSettingsManager
import com.juansicardo.monitor.sms.SendSMSPermissionRequester
import java.text.DateFormat
import java.util.*

class CreateProfileActivity : AppCompatActivity() {

    companion object {
        private const val DATE_PICKER_TAG = "com.juansicardo.monitor.create_profile_activity.date_picker"

        fun createIntent(context: Context) = Intent(context, CreateProfileActivity::class.java)
    }

    //Initialize date picker
    private val datePicker: MaterialDatePicker<Long>

    init {
        val today = MaterialDatePicker.todayInUtcMilliseconds()
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

        calendar.timeInMillis = today
        val constraintBuilder = CalendarConstraints.Builder().setValidator(DateValidatorPointBackward.before(today))

        datePicker = MaterialDatePicker.Builder.datePicker().setTitleText("").setSelection(today)
            .setCalendarConstraints(constraintBuilder.build()).build()

        datePicker.addOnPositiveButtonClickListener { birthdayTimestamp = it + 86400000 }
    }

    //Declare UI elements
    private lateinit var nameInputLayout: TextInputLayout
    private lateinit var nameEditText: EditText
    private lateinit var birthdayInputLayout: TextInputLayout
    private lateinit var birthdayEditText: EditText
    private lateinit var passwordInputLayout: TextInputLayout
    private lateinit var passwordEditText: EditText
    private lateinit var createProfileButton: Button
    private lateinit var loadingDialogFragment: LoadingDialogFragment

    //Initialize database
    private val dataBaseViewModel: DataBaseViewModel by lazy {
        ViewModelProvider(this).get(DataBaseViewModel::class.java)
    }

    //Elements read from database
    private lateinit var allProfilesLiveData: LiveData<List<Profile>>
    private lateinit var allProfiles: List<Profile>
    private lateinit var profile: Profile
    private var isProfileCreated = false

    //Settings management
    private lateinit var profileSettingsManager: ProfileSettingsManager

    //Permissions management
    private lateinit var smsServiceTogglePermissionRequester: SendSMSPermissionRequester

    //Birthday
    private var birthdayTimestamp: Long = 0
    private val birthdayDate: Date
        get() = Date(birthdayTimestamp)
    private val dateFormat = DateFormat.getDateInstance()
    private val birthdayText: String
        get() = dateFormat.format(birthdayDate)

    private val allProfilesNames: Set<String>
        get() {
            val set = mutableSetOf<String>()
            allProfiles.forEach { set.add(it.name) }
            return set.toSet()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_profile)

        loadingDialogFragment = LoadingDialogFragment(this)
        loadingDialogFragment.show()

        //Initialize UI elements
        nameInputLayout = findViewById(R.id.name_input_layout)
        nameEditText = findViewById(R.id.name_edit_text)
        birthdayInputLayout = findViewById(R.id.birthday_input_layout)
        birthdayEditText = findViewById(R.id.birthday_edit_text)
        passwordInputLayout = findViewById(R.id.password_input_layout)
        passwordEditText = findViewById(R.id.password_edit_text)
        createProfileButton = findViewById(R.id.create_profile_button)

        //Permission management
        smsServiceTogglePermissionRequester = SendSMSPermissionRequester(this) { isSendSmsPermissionGranted ->
            profileSettingsManager.setAreEmergencySMSEnabled(isSendSmsPermissionGranted)
        }

        //Get data from database
        allProfilesLiveData = dataBaseViewModel.allProfiles
        allProfilesLiveData.observe(this) { allProfiles ->
            this.allProfiles = allProfiles

            if (isProfileCreated) {
                val profileWithId = allProfiles.first { it.name == profile.name }
                profileSettingsManager = ProfileSettingsManager(this, profileWithId.profileId)
                smsServiceTogglePermissionRequester.request()

                allProfilesLiveData.removeObservers(this)
            }
        }
        loadingDialogFragment.dismiss()

        //Create profile
        createProfileButton.setOnClickListener {
            //Validate fields
            var areFieldsValid = true
            if (!isNameValid()) areFieldsValid = false
            if (!isPasswordValid()) areFieldsValid = false

            if (areFieldsValid) {
                val name = nameEditText.text.toString().trim()
                //TODO: Find a way to hash the string so it's more secure
                val password = passwordEditText.text.toString()

                isProfileCreated = true
                profile = Profile(0, name, birthdayTimestamp, password)
                dataBaseViewModel.addProfile(profile)

                Toast.makeText(this, getString(R.string.profile_created_succesfully), Toast.LENGTH_LONG).show()
                startActivity(EmergencyContactsActivity.createIntentByName(this, name))

                finish()
            }
        }

        //Action listeners
        datePicker.addOnPositiveButtonClickListener { birthdayTimestamp = it + 86400000 }

        birthdayEditText.setOnClickListener {
            datePicker.show(supportFragmentManager, DATE_PICKER_TAG)
        }

        datePicker.addOnPositiveButtonClickListener {
            birthdayTimestamp = it + 86400000
            updateBirthday()
        }

        updateBirthday()
    }

    //Update the text on birthday_input_layout's EditText
    private fun updateBirthday() {
        birthdayEditText.setText(birthdayText)
    }

    //Field Validation
    private fun isNameValid(): Boolean {
        val name = nameEditText.text.toString().trim()

        //Is empty
        if (name.isBlank()) {
            nameInputLayout.error = getString(R.string.field_required)
            return false
        }

        //Is alphanumerical
        if (name.filter { it.isLetterOrDigit() || it.isWhitespace() }.length != name.length) {
            nameInputLayout.error = getString(R.string.field_must_be_alphanumerical)
            return false
        }

        //Is too long
        if (name.length > ApplicationConstants.PROFILE_NAME_MAX_LENGTH) {
            nameInputLayout.error = getString(R.string.field_too_long, ApplicationConstants.PROFILE_NAME_MAX_LENGTH)
            return false
        }

        //Check uniqueness
        if (allProfilesNames.contains(name)) {
            nameInputLayout.error = getString(R.string.name_already_exists)
            return false
        }

        nameInputLayout.error = null
        return true
    }

    private fun isPasswordValid(): Boolean {
        val password = passwordEditText.text.toString()

        //Is empty
        if (password.isEmpty()) {
            passwordInputLayout.error = getString(R.string.field_required)
            return false
        }

        //Contains whitespaces
        if (password.any { it.isWhitespace() }) {
            passwordInputLayout.error = getString(R.string.field_must_not_contain_whitespaces)
            return false
        }

        //Is too long
        if (password.length > ApplicationConstants.PROFILE_PASSWORD_MAX_LENGTH) {
            passwordInputLayout.error =
                getString(R.string.field_too_long, ApplicationConstants.PROFILE_PASSWORD_MAX_LENGTH)
            return false
        }

        //TODO: check minimum size

        //TODO: check that password only contains letter, numbers and special characters

        //TODO: check that password is strong enough

        passwordInputLayout.error = null
        return true
    }
}