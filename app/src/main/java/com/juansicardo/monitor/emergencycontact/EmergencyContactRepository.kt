package com.juansicardo.monitor.emergencycontact

import androidx.lifecycle.LiveData
import com.juansicardo.monitor.emergencycontact.EmergencyContactDao
import com.juansicardo.monitor.emergencycontact.EmergencyContact

class EmergencyContactRepository(private val emergencyContactDao: EmergencyContactDao) {

    val allEmergencyContacts = emergencyContactDao.findAllEmergencyContacts()

    fun findEmergencyContactsOfProfile(profileOwnerId: Int): LiveData<List<EmergencyContact>> =
        emergencyContactDao.findEmergencyContactsOfProfile(profileOwnerId)

    fun findEmergencyContactById(id: Int) = emergencyContactDao.findEmergencyContactById(id)

    suspend fun addEmergencyContact(emergencyContact: EmergencyContact) =
        emergencyContactDao.insertEmergencyContact(emergencyContact)

    suspend fun updateEmergencyContact(emergencyContact: EmergencyContact) =
        emergencyContactDao.updateEmergencyContact(emergencyContact)

    suspend fun deleteEmergencyContact(emergencyContact: EmergencyContact) =
        emergencyContactDao.deleteEmergencyContact(emergencyContact)
}