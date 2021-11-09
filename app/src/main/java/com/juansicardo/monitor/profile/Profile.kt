package com.juansicardo.monitor.profile

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

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

)