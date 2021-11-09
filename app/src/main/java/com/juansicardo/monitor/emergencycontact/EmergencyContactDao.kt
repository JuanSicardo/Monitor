package com.juansicardo.monitor.emergencycontact

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface EmergencyContactDao {

    @Query("SELECT * FROM emergency_contacts")
    fun findAllEmergencyContacts(): LiveData<List<EmergencyContact>>

    @Query("SELECT * FROM emergency_contacts WHERE profile_owner_id = :profileOwnerId")
    fun findEmergencyContactsOfProfile(profileOwnerId: Int): LiveData<List<EmergencyContact>>

    @Query("SELECT * FROM emergency_contacts WHERE emergency_contact_id = :id")
    fun findEmergencyContactById(id: Int): LiveData<EmergencyContact>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEmergencyContact(emergencyContact: EmergencyContact)

    @Update
    suspend fun updateEmergencyContact(emergencyContact: EmergencyContact)

    @Delete
    suspend fun deleteEmergencyContact(emergencyContact: EmergencyContact)
}