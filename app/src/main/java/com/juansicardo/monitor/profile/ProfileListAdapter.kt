package com.juansicardo.monitor.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.juansicardo.monitor.R
import com.juansicardo.monitor.login.LoginActivity
import com.juansicardo.monitor.model.ListItem

class ProfileListAdapter(private val owner: AppCompatActivity, profilesListLiveData: LiveData<List<Profile>>) :
    RecyclerView.Adapter<ProfileListAdapter.ListItemViewHolder>() {

    private var profilesList: List<Profile>

    init {
        profilesList = listOf()
        profilesListLiveData.observe(owner) { profilesList ->
            this.profilesList = profilesList
            notifyDataSetChanged()
        }
    }

    class ListItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val optionCardView: MaterialCardView = view.findViewById(R.id.option_card_view)
        val optionImageView: ImageView = view.findViewById(R.id.option_image_view)
        val optionTextView: TextView = view.findViewById(R.id.option_text_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.option_view_holder, parent, false)
        return ListItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ListItemViewHolder, position: Int) {
        val profile = profilesList[position]
        val option = ListItem(R.drawable.icon_profile, profile.name)
        holder.optionImageView.setImageResource(option.imageResourceId)
        holder.optionTextView.text = option.text

        holder.optionCardView.setOnClickListener {
            owner.startActivity(LoginActivity.createIntent(owner, profile.profileId))
        }
    }

    override fun getItemCount(): Int = profilesList.size
}