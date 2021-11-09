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
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputLayout
import com.juansicardo.monitor.R
import com.juansicardo.monitor.constants.ApplicationConstants
import com.juansicardo.monitor.database.DataBaseViewModel
import com.juansicardo.monitor.dialog.ConfirmationDialogFragment
import com.juansicardo.monitor.dialog.LoadingDialogFragment
import com.juansicardo.monitor.sms.SendSMSPermissionRequester

class UpdateDeleteEmergencyContactActivity : AppCompatActivity(),
    ConfirmationDialogFragment.ConfirmationDialogListener {

    companion object {
        private const val EMERGENCY_CONTACT_ID_EXTRA_KEY =
            "com.juansicardo.monitor.update_delete_emergency_contact_activity.emergency_contact_id"


        fun createIntent(context: Context, emergencyContactId: Int) =
            Intent(context, UpdateDeleteEmergencyContactActivity::class.java).apply {
                this.putExtra(EMERGENCY_CONTACT_ID_EXTRA_KEY, emergencyContactId)
            }
    }

    //Declare UI elements
    private lateinit var nameInputLayout: TextInputLayout
    private lateinit var nameEditText: EditText
    private lateinit var phoneNumberInputLayout: TextInputLayout
    private lateinit var phoneNumberEditText: EditText
    private lateinit var updateEmergencyContactButton: Button
    private lateinit var deleteEmergencyContactButton: Button
    private lateinit var updateToast: Toast
    private lateinit var deleteToast: Toast
    private lateinit var loadingDialogFragment: LoadingDialogFragment
    private lateinit var updateConfirmationDialogFragment: ConfirmationDialogFragment
    private lateinit var deleteConfirmationDialogFragment: ConfirmationDialogFragment
    private lateinit var warningImageView: AppCompatImageView
    private lateinit var warningTextView: TextView

    //Initialize database
    private val dataBaseViewModel: DataBaseViewModel by lazy {
        ViewModelProvider(this).get(DataBaseViewModel::class.java)
    }

    //Elements read from database
    private lateinit var emergencyContactLiveData: LiveData<EmergencyContact>
    private lateinit var emergencyContact: EmergencyContact
    private lateinit var allProfileEmergencyContactsLiveData: LiveData<List<EmergencyContact>>
    private lateinit var allProfileEmergencyContacts: List<EmergencyContact>
    private lateinit var allEmergencyContacts: List<EmergencyContact>

    //Permission management
    private lateinit var sendSMSPermissionRequester: SendSMSPermissionRequester

    //Business logic
    private var isEmergencyContactUpdated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_delete_emergency_contact)

        loadingDialogFragment = LoadingDialogFragment(this)
        loadingDialogFragment.show()

        //Intent variables
        val emergencyContactId = intent.getIntExtra(EMERGENCY_CONTACT_ID_EXTRA_KEY, 0)

        //UI
        nameInputLayout = findViewById(R.id.name_input_layout)
        nameEditText = findViewById(R.id.name_edit_text)
        phoneNumberInputLayout = findViewById(R.id.phone_number_input_layout)
        phoneNumberEditText = findViewById(R.id.phone_number_edit_text)
        updateEmergencyContactButton = findViewById(R.id.update_emergency_contact_button)
        deleteEmergencyContactButton = findViewById(R.id.delete_emergency_contact_button)
        warningImageView = findViewById(R.id.warning_image_view)
        warningTextView = findViewById(R.id.warning_text_view)

        updateConfirmationDialogFragment =
            ConfirmationDialogFragment(this, getString(R.string.are_you_suere), "update_confirmation_dialog")
        deleteConfirmationDialogFragment =
            ConfirmationDialogFragment(this, getString(R.string.are_you_suere), "delete_confirmation_dialog")

        updateToast = Toast.makeText(this, getString(R.string.emergency_contact_updated), Toast.LENGTH_LONG)
        deleteToast = Toast.makeText(this, getString(R.string.emergency_contact_deleted), Toast.LENGTH_LONG)

        //Permission management
        sendSMSPermissionRequester = SendSMSPermissionRequester(this) { isPermissionGranted ->
            if (isPermissionGranted) {
                //UI initialization
                warningImageView.setImageResource(R.drawable.icon_info)
                warningTextView.setText(R.string.message_send_warning)

                //Send test message
                if (isEmergencyContactUpdated) {
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
                //UI initialization
                warningImageView.setImageResource(R.drawable.ic_warning_24)
                warningTextView.setText(R.string.no_send_message_permission_granted)
            }
        }
        sendSMSPermissionRequester.request()

        //Get from database
        emergencyContactLiveData = dataBaseViewModel.findEmergencyContactById(emergencyContactId)
        emergencyContactLiveData.observe(this) { emergencyContact ->
            this.emergencyContact = emergencyContact

            nameEditText.setText(emergencyContact.name)
            phoneNumberEditText.setText(emergencyContact.phoneNumber)

            allProfileEmergencyContactsLiveData =
                dataBaseViewModel.findEmergencyContactsOfProfile(emergencyContact.profileOwnerId)
            allProfileEmergencyContactsLiveData.observe(this) { allProfileEmergencyContacts ->
                this.allProfileEmergencyContacts = allProfileEmergencyContacts
                loadingDialogFragment.dismiss()
            }
        }

        //Action listeners
        updateEmergencyContactButton.setOnClickListener {
            var areFieldsValid = true
            if (!isNameValid()) areFieldsValid = false
            if (!isPhoneNumberValid()) areFieldsValid = false

            if (areFieldsValid) {
                updateConfirmationDialogFragment.show()
            }
        }

        deleteEmergencyContactButton.setOnClickListener {
            deleteConfirmationDialogFragment.show()
        }
    }

    //Validate name
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
        if (allProfileEmergencyContacts.any { it.name == name && it.emergencyContactId != emergencyContact.emergencyContactId }) {
            nameInputLayout.error = getString(R.string.name_already_exists)
            return false
        }
        nameInputLayout.error = null
        return true
    }

    //Validate phone number
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

    //Confirmation dialog actions
    override fun onDialogPositiveClick(dialog: DialogFragment) {
        when (dialog) {
            //Update emergency contact
            updateConfirmationDialogFragment -> {
                emergencyContact = EmergencyContact(
                    emergencyContact.emergencyContactId,
                    nameEditText.text.toString().trim(),
                    phoneNumberEditText.text.toString().trim(),
                    emergencyContact.profileOwnerId
                )
                dataBaseViewModel.updateEmergencyContact(emergencyContact)

                //Send confirmation message
                isEmergencyContactUpdated = true
                sendSMSPermissionRequester.request()

                updateToast.show()
                emergencyContactLiveData.removeObservers(this)
                allProfileEmergencyContactsLiveData.removeObservers(this)
                finish()
            }

            //Delete emergency contact
            deleteConfirmationDialogFragment -> {
                emergencyContactLiveData.removeObservers(this)
                allProfileEmergencyContactsLiveData.removeObservers(this)

                dataBaseViewModel.deleteEmergencyContact(emergencyContact)
                deleteToast.show()

                finish()
            }
        }
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {}
}