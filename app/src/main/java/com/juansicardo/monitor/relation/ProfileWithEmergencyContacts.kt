package com.juansicardo.monitor.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.juansicardo.monitor.emergencycontact.EmergencyContact
import com.juansicardo.monitor.profile.Profile

data class ProfileWithEmergencyContacts(
    @Embedded val profile: Profile,
    @Relation(
        parentColumn = "profile_id",
        entityColumn = "profile_owner_id"
    )
    val emergencyContacts: List<EmergencyContact>
)
