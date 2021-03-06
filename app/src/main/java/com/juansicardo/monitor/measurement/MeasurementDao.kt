package com.juansicardo.monitor.measurement

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MeasurementDao {

    @Query("SELECT * FROM measurements")
    fun findAllMeasurements(): LiveData<List<Measurement>>

    @Query("SELECT * FROM measurements WHERE profile_owner_id = :profileId")
    fun findMeasurementsByProfile(profileId: Int): LiveData<List<Measurement>>

    @Query("SELECT * FROM measurements WHERE profile_owner_id = :profileId AND measurement_type = :type")
    fun findMeasurementsByProfileAndType(profileId: Int, type: Int): LiveData<List<Measurement>>

    @Query("SELECT * FROM measurements WHERE profile_owner_id = :profileId AND measurement_type = :type AND date >= :start AND date <= :end")
    fun findMeasurementsByProfileTypeAndDate(
        profileId: Int,
        type: Int,
        start: Long,
        end: Long
    ): LiveData<List<Measurement>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMeasurement(measurement: Measurement)

    @Update
    suspend fun updateMeasurement(measurement: Measurement)

    @Delete
    suspend fun deleteMeasurement(measurement: Measurement)
}