package com.juansicardo.monitor.emergencycontact

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputLayout
import com.juansicardo.monitor.R
import com.juansicardo.monitor.constants.ApplicationConstants
import com.juansicardo.monitor.database.DataBaseViewModel
import com.juansicardo.monitor.dialog.LoadingDialogFragment
import com.juansicardo.monitor.profile.Profile
import com.juansicardo.monitor.sms.SendSMSPermissionRequester

class CreateEmergencyContactActivity : AppCompatActivity() {

    companion object {
        private const val PROFILE_ID_EXTRA_KEY = "com.juansicardo.monitor.create_emergency_contact_activity.profile_id"

        fun createIntent(context: Context, profileId: Int): Intent {
            val intent = Intent(context, CreateEmergencyContactActivity::class.java)
            intent.putExtra(PROFILE_ID_EXTRA_KEY, profileId)
            return intent
        }
    }

    //Declare UI elements
    private lateinit var nameInputLayout: TextInputLayout
    private lateinit var nameEditText: EditText
    private lateinit var phoneNumberInputLayout: TextInputLayout
    private lateinit var phoneNumberEditText: EditText
    private lateinit var createEmergencyContactButton: Button
    private lateinit var warningImageView: AppCompatImageView
    private lateinit var warningTextView: TextView
    private lateinit var loadingDialogFragment: LoadingDialogFragment

    //Database initialization
    private val dataBaseViewModel: DataBaseViewModel by lazy {
        ViewModelProvider(this).get(DataBaseViewModel::class.java)
    }

    //Database related elements
    private lateinit var profileLiveData: LiveData<Profile>
    private lateinit var profile: Profile
    private lateinit var allProfileEmergencyContactsLiveData: LiveData<List<EmergencyContact>>
    private lateinit var allProfileEmergencyContacts: List<EmergencyContact>

    //Business logic
    private lateinit var emergencyContact: EmergencyContact
    private var isEmergencyContactCreated = false

    //Permission management
    private lateinit var sendSMSPermissionRequester: SendSMSPermissionRequester

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_emergency_contact)

        loadingDialogFragment = LoadingDialogFragment(this)
        loadingDialogFragment.show()

        //UI elements initialization
        nameInputLayout = findViewById(R.id.name_input_layout)
        nameEditText = findViewById(R.id.name_edit_text)
        phoneNumberInputLayout = findViewById(R.id.phone_number_input_layout)
        phoneNumberEditText = findViewById(R.id.phone_number_edit_text)
        createEmergencyContactButton = findViewById(R.id.create_emergency_contact_button)
        warningImageView = findViewById(R.id.warning_image_view)
        warningTextView = findViewById(R.id.warning_text_view)

        //Intent variables
        val profileId = intent.getIntExtra(PROFILE_ID_EXTRA_KEY, 0)

        //Database elements initialization
        profileLiveData = dataBaseViewModel.findProfileById(profileId)
        profileLiveData.observe(this) { profile ->
            this.profile = profile

            allProfileEmergencyContactsLiveData = dataBaseViewModel.findEmergencyContactsOfProfile(profileId)
            allProfileEmergencyContactsLiveData.observe(this) { allProfileEmergencyContacts ->
                this.allProfileEmergencyContacts = allProfileEmergencyContacts

                loadingDialogFragment.dismiss()
            }
        }

        //Permission management
        sendSMSPermissionRequester = SendSMSPermissionRequester(this) { isPermissionGranted ->
            if (isPermissionGranted) {
                //Initialize UI
                warningImageView.setImageResource(R.drawable.icon_info)
                warningTextView.setText(R.string.message_send_warning)

                //Send test message
                if (isEmergencyContactCreated) {
                    val sms = SmsManager.getDefault()
                    sms.sendTextMessage(
                        emergencyContact.phoneNumber,
                        "MonitorApp",
                        getString(R.string.emergency_contact_confirmation_message),
                        null,
                        null
                    )
                }
            } else {
                //Initialize UI
                warningImageView.setImageResource(R.drawable.ic_warning_24)
                warningTextView.setText(R.string.no_send_message_permission_granted)
            }
        }
        sendSMSPermissionRequester.request()

        //Action listeners
        createEmergencyContactButton.setOnClickListener {
            //Validations
            var areFieldsValid = true
            if (!isNameValid()) areFieldsValid = false
            if (!isPhoneNumberValid()) areFieldsValid = false

            if (areFieldsValid) {
                val name = nameEditText.text.toString().trim()
                val phoneNumber = phoneNumberEditText.text.toString().trim()

                emergencyContact = EmergencyContact(0, name, phoneNumber, profile.profileId)
                dataBaseViewModel.addEmergencyContact(emergencyContact)

                isEmergencyContactCreated = true
                sendSMSPermissionRequester.request()

                Toast.makeText(this, getString(R.string.emergency_contact_created_successfully), Toast.LENGTH_LONG)
                    .show()

                //Remove observers
                profileLiveData.removeObservers(this)
                allProfileEmergencyContactsLiveData.removeObservers(this)

                finish()
            }
        }
    }

    //Validate fields
    private fun isNameValid(): Boolean {
        val name = nameEditText.text.toString().trim()

        //check emptiness
        if (name.isBlank()) {
            nameInputLayout.error = getString(R.string.field_required)
            return false
        }

        //check format
        if (name.filter { it.isLetterOrDigit() || it.isWhitespace() }.length != name.length) {
            nameInputLayout.error = getString(R.string.field_must_be_alphanumerical)
            return false
        }

        //check length
        if (name.length > ApplicationConstants.PROFILE_NAME_MAX_LENGTH) {
            nameInputLayout.error = getString(R.string.field_too_long, ApplicationConstants.PROFILE_NAME_MAX_LENGTH)
            return false
        }

        //check uniqueness
        if (allProfileEmergencyContacts.any { it.name == name }) {
            nameInputLayout.error = getString(R.string.name_already_exists)
            return false
        }

        nameInputLayout.error = null
        return true
    }

    private fun isPhoneNumberValid(): Boolean {
        val phone = phoneNumberEditText.text.toString().trim()

        //check emptiness
        if (phone.isBlank()) {
            phoneNumberInputLayout.error = getString(R.string.field_required)
            return false
        }

        //check format
        if (phone.filter {
                it.isLetterOrDigit() || it == '+' || it == '-' || it == '.' || it == ',' || it == '(' || it == ')'
            }.length != phone.length) {
            phoneNumberInputLayout.error = getString(R.string.invalid_format)
            return false
        }

        //check length
        if (phone.length > ApplicationConstants.PHONE_NUMBER_MAX_LENGTH) {
            phoneNumberInputLayout.error =
                getString(R.string.field_too_long, ApplicationConstants.PHONE_NUMBER_MAX_LENGTH)
            return false
        }

        phoneNumberInputLayout.error = null
        return true
    }
}