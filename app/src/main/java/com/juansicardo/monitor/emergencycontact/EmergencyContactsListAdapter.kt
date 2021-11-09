package com.juansicardo.monitor.emergencycontact

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
import com.juansicardo.monitor.model.ListItem

class EmergencyContactsListAdapter(
    private val owner: AppCompatActivity,
    emergencyContactListLiveData: LiveData<List<EmergencyContact>>
) : RecyclerView.Adapter<EmergencyContactsListAdapter.ListItemViewHolder>() {

    private var emergencyContactsList: List<EmergencyContact>

    init {
        emergencyContactsList = listOf()
        emergencyContactListLiveData.observe(owner) { emergencyContactsList ->
            this.emergencyContactsList = emergencyContactsList
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
        val emergencyContact = emergencyContactsList[position]
        val option = ListItem(R.drawable.ic_phone_30dp, emergencyContact.name)
        holder.optionImageView.setImageResource(option.imageResourceId)
        holder.optionTextView.text = option.text

        holder.optionCardView.setOnClickListener {
            owner.startActivity(
                UpdateDeleteEmergencyContactActivity.createIntent(
                    owner,
                    emergencyContact.emergencyContactId
                )
            )
        }
    }

    override fun getItemCount(): Int = emergencyContactsList.size


}