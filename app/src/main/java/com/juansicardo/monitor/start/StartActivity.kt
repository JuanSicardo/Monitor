package com.juansicardo.monitor.start

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.juansicardo.monitor.R
import com.juansicardo.monitor.database.DataBaseViewModel
import com.juansicardo.monitor.notification.NotificationSettingsActivity
import com.juansicardo.monitor.profile.CreateProfileActivity
import com.juansicardo.monitor.profile.ProfileListAdapter

//Launch activity

class StartActivity : AppCompatActivity() {
    //Declare UI elements
    private lateinit var profileRecyclerView: RecyclerView
    private lateinit var createProfileCardView: MaterialCardView

    //Declare database view model
    private val dataBaseViewModel: DataBaseViewModel by lazy {
        ViewModelProvider(this).get(DataBaseViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        //Initialize UI elements
        profileRecyclerView = findViewById(R.id.profile_recycler_view)
        createProfileCardView = findViewById(R.id.create_profile_card_view)

        //Initialize profile recycler view
        profileRecyclerView.adapter = ProfileListAdapter(this, dataBaseViewModel.allProfiles)

        //UI action listeners
        createProfileCardView.setOnClickListener {
            startActivity(CreateProfileActivity.createIntent(this))
        }
    }

    //Top menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.title_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.global_settings_menu_item -> {
                startActivity(NotificationSettingsActivity.createIntent(this))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}