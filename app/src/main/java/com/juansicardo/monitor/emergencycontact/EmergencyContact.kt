package com.juansicardo.monitor.emergencycontact

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "emergency_contacts")
data class EmergencyContact(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "emergency_contact_id")
    val emergencyContactId: Int,

    @ColumnInfo(name = "emergency_contact_name")
    val name: String,

    @ColumnInfo(name = "phone_number")
    val phoneNumber: String,

    @ColumnInfo(name = "profile_owner_id")
    val profileOwnerId: Int
)