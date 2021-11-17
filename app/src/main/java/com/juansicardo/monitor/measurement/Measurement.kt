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
        val timeOfDay = (date - 21600000) % 86400000
        val x = (timeOfDay.toFloat() / 86400000.toFloat()) * 24.toFloat()

        return Entry(x, y)
    }
}