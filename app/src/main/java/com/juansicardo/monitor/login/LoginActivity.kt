package com.juansicardo.monitor.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputLayout
import com.juansicardo.monitor.R
import com.juansicardo.monitor.dialog.LoadingDialogFragment
import com.juansicardo.monitor.profile.Profile
import com.juansicardo.monitor.home.HomeActivity
import com.juansicardo.monitor.database.DataBaseViewModel

class LoginActivity : AppCompatActivity() {

    companion object {
        private const val PROFILE_ID_EXTRA_KEY = "com.juansicardo.monitor.login_activity.profile_id"

        fun createIntent(context: Context, profileId: Int) = Intent(context, LoginActivity::class.java).apply {
            this.putExtra(PROFILE_ID_EXTRA_KEY, profileId)
        }
    }

    //Declare UI elements
    private lateinit var nameTextView: TextView
    private lateinit var passwordInputLayout: TextInputLayout
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var loadingDialogFragment: LoadingDialogFragment

    //Initialize database
    private val databaseViewModel by lazy {
        ViewModelProvider(this).get(DataBaseViewModel::class.java)
    }

    //Objects read from database
    private lateinit var profileLiveData: LiveData<Profile>
    private lateinit var profile: Profile

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loadingDialogFragment = LoadingDialogFragment(this)
        loadingDialogFragment.show()

        //Intent variables
        val profileId = intent.getIntExtra(PROFILE_ID_EXTRA_KEY, 0)

        //Initialize UI elements
        nameTextView = findViewById(R.id.name)
        passwordInputLayout = findViewById(R.id.password_input_layout)
        passwordEditText = findViewById(R.id.password_edit_text)
        loginButton = findViewById(R.id.login_button)

        //Get from database
        profileLiveData = databaseViewModel.findProfileById(profileId)
        profileLiveData.observe(this) { profile ->
            this.profile = profile
            nameTextView.text = profile.name
            loadingDialogFragment.dismiss()
        }

        loginButton.setOnClickListener {
            //Field validation
            if (isPasswordValid() && isPasswordCorrect()) {
                startActivity(HomeActivity.createIntent(this, profile.profileId))
                profileLiveData.removeObservers(this)
                finish()
            }
        }
    }

    //Validate password
    private fun isPasswordValid(): Boolean {
        val password = passwordEditText.text.toString()
        //Check emptiness
        if (password.isEmpty()) {
            passwordInputLayout.error = getString(R.string.field_required)
            return false
        }

        passwordInputLayout.error = null
        return true
    }

    private fun isPasswordCorrect(): Boolean {
        val password = passwordEditText.text.toString()

        if (password != profile.passwordHash) {
            passwordInputLayout.error = getString(R.string.wrong_password)
            return false
        }

        passwordInputLayout.error = null
        return true
    }
}