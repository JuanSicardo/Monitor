package com.juansicardo.monitor.profile

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//TODO: add Date and string representations of the birthday

@Entity(tableName = "profiles")
data class Profile(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "profile_id")
    val profileId: Int,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "birthday")
    val birthdayTimestamp: Long,

    @ColumnInfo(name = "password_hash")
    val passwordHash: String

) {
    val maxHeartRate: Int
        get() {
            val ageInMillis = System.currentTimeMillis() - birthdayTimestamp
            val ageInDays = (ageInMillis / 86400000L).toInt() + 1
            val ageInYears = (ageInDays / 365)
            return (208.0 - 0.7 * ageInYears.toDouble()).toInt()
        }
}