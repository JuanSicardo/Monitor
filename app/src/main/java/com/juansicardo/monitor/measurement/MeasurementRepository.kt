package com.juansicardo.monitor.measurement

class MeasurementRepository(private val measurementDao: MeasurementDao) {

    val allMeasurements = measurementDao.findAllMeasurements()

    fun findMeasurementsByProfileAndType(profileId: Int, typeId: Int) =
        measurementDao.findMeasurementsByProfileAndType(profileId, typeId)

    fun findMeasurementsByProfile(profileId: Int) = measurementDao.findMeasurementsByProfile(profileId)

    fun findMeasurementsByProfileTypeAndDate(profileId: Int, type: Int, start: Long, end: Long) =
        measurementDao.findMeasurementsByProfileTypeAndDate(profileId, type, start, end)

    suspend fun insertMeasurement(measurement: Measurement) = measurementDao.insertMeasurement(measurement)

    suspend fun updateMeasurement(measurement: Measurement) = measurementDao.updateMeasurement(measurement)

    suspend fun deleteMeasurement(measurement: Measurement) = measurementDao.deleteMeasurement(measurement)
}