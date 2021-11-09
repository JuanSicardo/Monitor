package com.juansicardo.monitor.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.switchmaterial.SwitchMaterial
import com.juansicardo.monitor.R
import com.juansicardo.monitor.emergencycontact.EmergencyContactsActivity
import com.juansicardo.monitor.home.HomeViewModel
import com.juansicardo.monitor.profile.Profile
import com.juansicardo.monitor.profile.UpdateDeleteProfileActivity

class SettingsFragment : Fragment() {

    //Declare UI elements
    private lateinit var editProfileCardView: CardView
    private lateinit var emergencyContactsCardView: CardView
    private lateinit var emergencyMessagesCardView: CardView
    private lateinit var logoutCardView: CardView
    private lateinit var emergencyMessagesSwitch: SwitchMaterial

    //Permission management
    private var isSendSMSPermissionGranted = false

    //Settings
    private lateinit var profileSettingsManager: ProfileSettingsManager

    //Business logic
    private lateinit var profile: Profile
    private val viewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.settings_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Initialize UI
        editProfileCardView = view.findViewById(R.id.edit_profile_card_view)
        emergencyContactsCardView = view.findViewById(R.id.emergency_contacts_card_view)
        emergencyMessagesCardView = view.findViewById(R.id.emergency_messages_card_view)
        logoutCardView = view.findViewById(R.id.logout_card_view)
        emergencyMessagesSwitch = view.findViewById(R.id.emergency_messages_switch)

        //Extract data from parent activity
        viewModel.profile.observe(viewLifecycleOwner) { profile ->
            this.profile = profile

            viewModel.isSendSMSPermissionGranted.observe(viewLifecycleOwner) { isSendSMSPermissionGranted ->
                this.isSendSMSPermissionGranted = isSendSMSPermissionGranted

                viewModel.profileSettingsManager.observe(viewLifecycleOwner) { profileSettingsManager ->
                    this.profileSettingsManager = profileSettingsManager
                    initMessageOption()
                }
            }
        }

        //Go to profile update and delete activity
        editProfileCardView.setOnClickListener {
            startActivity(UpdateDeleteProfileActivity.createIntent(requireContext(), profile.profileId))
        }

        //Go to emergency contacts activity
        emergencyContactsCardView.setOnClickListener {
            startActivity(EmergencyContactsActivity.createIntentById(requireContext(), profile.profileId))
        }

        //Toggle emergency messages service
        emergencyMessagesCardView.setOnClickListener {
            setAreEmergencySMSEnabled(!getAreEmergencySMSEnabled())
        }

        //Logout
        logoutCardView.setOnClickListener {
            activity?.finish()
        }
    }

    private fun getAreEmergencySMSEnabled() = profileSettingsManager.getAreEmergencySMSEnabled()

    private fun setAreEmergencySMSEnabled(areEmergencySMSEnabled: Boolean) {
        emergencyMessagesSwitch.isChecked = areEmergencySMSEnabled
        profileSettingsManager.setAreEmergencySMSEnabled(areEmergencySMSEnabled)
    }

    //Initializes the message option in the UI
    private fun initMessageOption() {
        if (isSendSMSPermissionGranted) {
            emergencyMessagesSwitch.isChecked = getAreEmergencySMSEnabled()

        } else {
            setAreEmergencySMSEnabled(false)

            //Tell user they cant use emergency messages
            emergencyMessagesCardView.setOnClickListener {
                Toast.makeText(requireContext(), R.string.no_send_message_permission_granted, Toast.LENGTH_LONG).show()
            }
        }
    }
}