package com.juansicardo.monitor.emergencycontact

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.juansicardo.monitor.R
import com.juansicardo.monitor.database.DataBaseViewModel
import com.juansicardo.monitor.dialog.LoadingDialogFragment
import com.juansicardo.monitor.profile.Profile

class EmergencyContactsActivity : AppCompatActivity() {

    companion object {
        private const val PROFILE_NAME_EXTRA_KEY = "com.juansicardo.monitor.emergency_contacts_activity.profile_name"
        private const val PROFILE_ID_EXTRA_KEY = "com.juansicardo.monitor.emergency_contacts_activity.profile_id"

        fun createIntentByName(context: Context, profileName: String): Intent {
            val intent = Intent(context, EmergencyContactsActivity::class.java)
            intent.putExtra(PROFILE_NAME_EXTRA_KEY, profileName)
            return intent
        }

        fun createIntentById(context: Context, profileId: Int) =
            Intent(context, EmergencyContactsActivity::class.java).apply {
                putExtra(PROFILE_ID_EXTRA_KEY, profileId)
            }
    }

    //Declare UI elements
    private lateinit var emergencyContactsRecyclerView: RecyclerView
    private lateinit var createEmergencyContactCardView: MaterialCardView
    private lateinit var continueButton: Button
    private lateinit var loadingDialogFragment: LoadingDialogFragment

    //Database
    private val dataBaseViewModel: DataBaseViewModel by lazy {
        ViewModelProvider(this).get(DataBaseViewModel::class.java)
    }

    //Database related elements
    private lateinit var allProfilesLiveData: LiveData<List<Profile>>
    private lateinit var profile: Profile

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emergency_contacts)

        loadingDialogFragment = LoadingDialogFragment(this)
        loadingDialogFragment.show()

        //Intent variables
        val profileName = intent.getStringExtra(PROFILE_NAME_EXTRA_KEY)
        val profileId = intent.getIntExtra(PROFILE_ID_EXTRA_KEY, -1)

        //Initialize UI elements
        emergencyContactsRecyclerView = findViewById(R.id.emergency_contacts_option_list)
        createEmergencyContactCardView = findViewById(R.id.add_emergency_contact_button)
        continueButton = findViewById(R.id.emergency_contacts_continue_button)

        //If the profile name is passed by parent activity
        profileName?.let {
            allProfilesLiveData = dataBaseViewModel.allProfiles
            allProfilesLiveData.observe(this) { profileList ->
                profile = profileList.first { profile -> profile.name == profileName }
                emergencyContactsRecyclerView.adapter =
                    EmergencyContactsListAdapter(this, dataBaseViewModel.findEmergencyContactsOfProfile(profile.profileId))
            }
        }

        //If the profile id is passed by parent activity
        if (profileId != -1) {
            dataBaseViewModel.findProfileById(profileId).observe(this) { profile ->
                this.profile = profile
                emergencyContactsRecyclerView.adapter =
                    EmergencyContactsListAdapter(this, dataBaseViewModel.findEmergencyContactsOfProfile(profile.profileId))
            }
        }

        loadingDialogFragment.dismiss()

        //Action listeners
        createEmergencyContactCardView.setOnClickListener {
            startActivity(CreateEmergencyContactActivity.createIntent(this, profile.profileId))
        }

        continueButton.setOnClickListener {
            finish()
        }
    }
}