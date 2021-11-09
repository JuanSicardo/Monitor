package com.juansicardo.monitor.measurement

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "measurements")
data class Measurement(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "measurement_id")
    val measurementId: Int,

    @ColumnInfo(name = "measurement_type")
    val measurementType: Int,

    @ColumnInfo(name = "value")
    val value: Int,

    @ColumnInfo(name = "date")
    val date: Long,

    @ColumnInfo(name = "profile_owner_id")
    val profileOwnerId: Int
)