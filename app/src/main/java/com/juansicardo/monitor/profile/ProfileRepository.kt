package com.juansicardo.monitor.profile

class ProfileRepository(private val profileDao: ProfileDao) {

    val allProfiles = profileDao.findAllProfiles()

    fun findAllProfilesWithEmergencyContacts() = profileDao.getAllProfilesWithEmergencyContacts()

    fun findProfileById(id: Int) = profileDao.findProfileById(id)

    suspend fun addProfile(profile: Profile) = profileDao.insertProfile(profile)

    suspend fun updateProfile(profile: Profile) = profileDao.updateProfile(profile)

    suspend fun deleteProfile(profile: Profile) = profileDao.deleteProfile(profile)
}