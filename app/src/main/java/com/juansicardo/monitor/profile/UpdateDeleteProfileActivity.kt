package com.juansicardo.monitor.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import com.juansicardo.monitor.R
import com.juansicardo.monitor.constants.ApplicationConstants
import com.juansicardo.monitor.database.DataBaseViewModel
import com.juansicardo.monitor.dialog.ConfirmationDialogFragment
import com.juansicardo.monitor.dialog.LoadingDialogFragment
import com.juansicardo.monitor.emergencycontact.EmergencyContact
import com.juansicardo.monitor.relation.ProfileWithEmergencyContacts
import com.juansicardo.monitor.settings.ProfileSettingsManager
import java.text.DateFormat
import java.util.*

class UpdateDeleteProfileActivity : AppCompatActivity(), ConfirmationDialogFragment.ConfirmationDialogListener {

    companion object {
        private const val PROFILE_ID_EXTRA_KEY = "com.juansicardo.monitor.update_delete_profile_activity.profile_id"
        private const val DATE_PICKER_TAG = "com.juansicardo.monitor.update_delete_profile_activity.date_picker"

        fun createIntent(context: Context, profileId: Int) =
            Intent(context, UpdateDeleteProfileActivity::class.java).apply {
                putExtra(PROFILE_ID_EXTRA_KEY, profileId)
            }
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
    private lateinit var updateProfileButton: Button
    private lateinit var deleteProfileButton: Button
    private lateinit var loadingDialogFragment: LoadingDialogFragment
    private lateinit var updateConfirmationDialogFragment: ConfirmationDialogFragment
    private lateinit var deleteConfirmationDialogFragment: ConfirmationDialogFragment
    private lateinit var updateToast: Toast
    private lateinit var deleteToast: Toast

    //Initialize database
    private val dataBaseViewModel: DataBaseViewModel by lazy {
        ViewModelProvider(this).get(DataBaseViewModel::class.java)
    }

    //Elements read from database
    private lateinit var allProfilesLiveData: LiveData<List<Profile>>
    private lateinit var allProfiles: List<Profile>
    private lateinit var allProfilesWithEmergencyContactsLiveData: LiveData<List<ProfileWithEmergencyContacts>>
    private lateinit var profile: Profile
    private lateinit var emergencyContacts: List<EmergencyContact>

    //Settings
    private lateinit var profileSettingsManager: ProfileSettingsManager

    //Birthday
    private var birthdayTimestamp: Long = 0
    private val birthdayDate: Date
        get() = Date(birthdayTimestamp)
    private val dateFormat = DateFormat.getDateInstance()
    private val birthdayText: String
        get() = dateFormat.format(birthdayDate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_delete_profile)

        //Get current profile
        loadingDialogFragment = LoadingDialogFragment(this)
        loadingDialogFragment.show()

        //Get intent variables
        val profileId = intent.getIntExtra(PROFILE_ID_EXTRA_KEY, 0)

        //Initialize UI elements
        nameInputLayout = findViewById(R.id.name_input_layout)
        nameEditText = findViewById(R.id.name_edit_text)
        birthdayInputLayout = findViewById(R.id.birthday_input_layout)
        birthdayEditText = findViewById(R.id.birthday_edit_text)
        updateProfileButton = findViewById(R.id.update_profile_button)
        deleteProfileButton = findViewById(R.id.delete_profile_button)

        updateConfirmationDialogFragment =
            ConfirmationDialogFragment(this, getString(R.string.are_you_suere), "update_confirmation_dialog")
        deleteConfirmationDialogFragment =
            ConfirmationDialogFragment(this, getString(R.string.are_you_suere), "delete_confirmation_dialog")

        updateToast = Toast.makeText(this, getString(R.string.profile_updated), Toast.LENGTH_LONG)
        deleteToast = Toast.makeText(this, getString(R.string.profile_deleted), Toast.LENGTH_LONG)

        //Get data from database
        allProfilesLiveData = dataBaseViewModel.allProfiles
        allProfilesLiveData.observe(this) { allProfiles ->
            this.allProfiles = allProfiles

            allProfilesWithEmergencyContactsLiveData = dataBaseViewModel.findAllProfilesWithEmergencyContacts()
            allProfilesWithEmergencyContactsLiveData.observe(this) { allProfilesWithEmergencyContacts ->
                val profileWithEmergencyContacts =
                    allProfilesWithEmergencyContacts.first { profileWithEmergencyContacts ->
                        profileWithEmergencyContacts.profile.profileId == profileId
                    }
                profile = profileWithEmergencyContacts.profile
                emergencyContacts = profileWithEmergencyContacts.emergencyContacts

                birthdayTimestamp = profile.birthdayTimestamp

                //Show profile data
                nameEditText.setText(profile.name)
                birthdayEditText.setText(birthdayText)

                loadingDialogFragment.dismiss()
            }
        }

        //Action listeners
        updateProfileButton.setOnClickListener {
            if (isNameValid())
                updateConfirmationDialogFragment.show()
        }

        deleteProfileButton.setOnClickListener {
            deleteConfirmationDialogFragment.show()
        }

        birthdayEditText.setOnClickListener {
            datePicker.show(supportFragmentManager, DATE_PICKER_TAG)
        }

        datePicker.addOnPositiveButtonClickListener {
            birthdayTimestamp = it + 86400000
            birthdayEditText.setText(birthdayText)
        }
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
        if (allProfiles.any { (profile.profileId != it.profileId).and(name == it.name) }) {
            nameInputLayout.error = getString(R.string.name_already_exists)
            return false
        }

        nameInputLayout.error = null
        return true
    }

    override fun onDialogPositiveClick(dialog: DialogFragment) {
        when (dialog) {
            //Update profile
            updateConfirmationDialogFragment -> {
                allProfilesLiveData.removeObservers(this)
                allProfilesWithEmergencyContactsLiveData.removeObservers(this)

                val name = nameEditText.text.toString().trim()
                dataBaseViewModel.updateProfile(
                    Profile(
                        profile.profileId,
                        name,
                        birthdayTimestamp,
                        profile.passwordHash
                    )
                )

                updateToast.show()
                finish()
            }

            //Delete profile and its emergency contacts
            deleteConfirmationDialogFragment -> {
                allProfilesLiveData.removeObservers(this)
                allProfilesWithEmergencyContactsLiveData.removeObservers(this)

                emergencyContacts.forEach { emergencyContact ->
                    dataBaseViewModel.deleteEmergencyContact(emergencyContact)
                }
                dataBaseViewModel.deleteProfile(profile)

                profileSettingsManager = ProfileSettingsManager(this, profile.profileId)
                profileSettingsManager.deleteAreEmergencySMSEnabled()

                deleteToast.show()
                finish()
            }
        }
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {}
}