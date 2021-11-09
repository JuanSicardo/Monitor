package com.juansicardo.monitor.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.juansicardo.monitor.emergencycontact.EmergencyContact
import com.juansicardo.monitor.emergencycontact.EmergencyContactDao
import com.juansicardo.monitor.measurement.Measurement
import com.juansicardo.monitor.measurement.MeasurementDao
import com.juansicardo.monitor.profile.Profile
import com.juansicardo.monitor.profile.ProfileDao

@Database(entities = [Profile::class, EmergencyContact::class, Measurement::class], version = 4, exportSchema = false)
abstract class MonitorDatabase : RoomDatabase() {

    abstract fun profileDao(): ProfileDao
    abstract fun emergencyContactDao(): EmergencyContactDao
    abstract fun measurementDao(): MeasurementDao

    companion object {
        @Volatile
        private var INSTANCE: MonitorDatabase? = null

        fun getDatabase(context: Context): MonitorDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null)
                return tempInstance

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MonitorDatabase::class.java, "monitor_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }
        }
    }
}