package com.juansicardo.monitor.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.juansicardo.monitor.emergencycontact.EmergencyContact
import com.juansicardo.monitor.emergencycontact.EmergencyContactRepository
import com.juansicardo.monitor.measurement.Measurement
import com.juansicardo.monitor.measurement.MeasurementRepository
import com.juansicardo.monitor.profile.Profile
import com.juansicardo.monitor.profile.ProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DataBaseViewModel(application: Application) : AndroidViewModel(application) {

    //Profile
    val allProfiles: LiveData<List<Profile>>
    private val profileRepository: ProfileRepository

    //EmergencyContact
    private val allEmergencyContacts: LiveData<List<EmergencyContact>>
    private val emergencyContactRepository: EmergencyContactRepository

    //Measurement
    private val measurementRepository: MeasurementRepository

    init {
        val monitorDatabase = MonitorDatabase.getDatabase(application)

        //Profile
        val profileDao = monitorDatabase.profileDao()
        profileRepository = ProfileRepository(profileDao)
        allProfiles = profileRepository.allProfiles

        //EmergencyContact
        val emergencyContactDao = monitorDatabase.emergencyContactDao()
        emergencyContactRepository = EmergencyContactRepository(emergencyContactDao)
        allEmergencyContacts = emergencyContactRepository.allEmergencyContacts

        //Measurement
        val measurementDao = monitorDatabase.measurementDao()
        measurementRepository = MeasurementRepository(measurementDao)
    }

    //Profile
    fun findProfileById(id: Int) = profileRepository.findProfileById(id)

    fun findAllProfilesWithEmergencyContacts() = profileRepository.findAllProfilesWithEmergencyContacts()

    fun addProfile(profile: Profile) {
        viewModelScope.launch(Dispatchers.IO) {
            profileRepository.addProfile(profile)
        }
    }

    fun updateProfile(profile: Profile) {
        viewModelScope.launch(Dispatchers.IO) {
            profileRepository.updateProfile(profile)
        }
    }

    fun deleteProfile(profile: Profile) {
        viewModelScope.launch(Dispatchers.IO) {
            profileRepository.deleteProfile(profile)
        }
    }

    //EmergencyContact
    fun findEmergencyContactsOfProfile(profileOwnerId: Int): LiveData<List<EmergencyContact>> =
        emergencyContactRepository.findEmergencyContactsOfProfile(profileOwnerId)

    fun findEmergencyContactById(id: Int) = emergencyContactRepository.findEmergencyContactById(id)

    fun addEmergencyContact(emergencyContact: EmergencyContact) {
        viewModelScope.launch(Dispatchers.IO) {
            emergencyContactRepository.addEmergencyContact(emergencyContact)
        }
    }

    fun updateEmergencyContact(emergencyContact: EmergencyContact) {
        viewModelScope.launch(Dispatchers.IO) {
            emergencyContactRepository.updateEmergencyContact(emergencyContact)
        }
    }

    fun deleteEmergencyContact(emergencyContact: EmergencyContact) {
        viewModelScope.launch(Dispatchers.IO) {
            emergencyContactRepository.deleteEmergencyContact(emergencyContact)
        }
    }

    //Measurement
    fun findMeasurementsByProfile(profileId: Int) = measurementRepository.findMeasurementsByProfile(profileId)

    fun findMeasurementsByProfileAndType(profileId: Int, type: Int) =
        measurementRepository.findMeasurementsByProfileAndType(profileId, type)

    fun insertMeasurement(measurement: Measurement) {
        viewModelScope.launch(Dispatchers.IO) {
            measurementRepository.insertMeasurement(measurement)
        }
    }

    fun updateMeasurement(measurement: Measurement) {
        viewModelScope.launch(Dispatchers.IO) {
            measurementRepository.updateMeasurement(measurement)
        }
    }

    fun deleteMeasurement(measurement: Measurement) {
        viewModelScope.launch(Dispatchers.IO) {
            measurementRepository.deleteMeasurement(measurement)
        }
    }
}