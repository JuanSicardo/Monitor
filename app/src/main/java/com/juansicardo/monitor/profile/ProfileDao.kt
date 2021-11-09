package com.juansicardo.monitor.profile

import androidx.lifecycle.LiveData
import androidx.room.*
import com.juansicardo.monitor.relation.ProfileWithEmergencyContacts

@Dao
interface ProfileDao {

    @Query("SELECT * FROM profiles")
    fun findAllProfiles(): LiveData<List<Profile>>

    @Query("SELECT * FROM profiles WHERE profile_id = :id")
    fun findProfileById(id: Int): LiveData<Profile>

    @Transaction
    @Query("SELECT * FROM profiles")
    fun getAllProfilesWithEmergencyContacts(): LiveData<List<ProfileWithEmergencyContacts>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProfile(profile: Profile)

    @Update
    suspend fun updateProfile(profile: Profile)

    @Delete
    suspend fun deleteProfile(profile: Profile)
}