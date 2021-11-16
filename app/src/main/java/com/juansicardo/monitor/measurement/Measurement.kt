package com.juansicardo.monitor.measurement

import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.mikephil.charting.data.Entry
import com.juansicardo.monitor.constants.ApplicationConstants

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
) {
    fun toChartEntry(): Entry {
        val y = value.toFloat()

        Log.d(ApplicationConstants.APP_TAG, "Date recorded in the database: $date")
        val timeOfDay = date % 86400000
        Log.d(ApplicationConstants.APP_TAG, "Time of the day in long: $timeOfDay")
        val x = (timeOfDay.toFloat() / 86400000.toFloat()) * 100.0.toFloat()
        Log.d(ApplicationConstants.APP_TAG, "x: $x")

        return Entry(x, y)
    }
}