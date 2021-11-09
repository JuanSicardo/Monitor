package com.juansicardo.monitor.measurement

class MeasurementRepository(private val measurementDao: MeasurementDao) {

    val allMeasurements = measurementDao.findAllMeasurements()

    fun findMeasurementsByProfileAndType(profileId: Int, typeId: Int) =
        measurementDao.findMeasurementsByProfileAndType(profileId, typeId)

    suspend fun insertMeasurement(measurement: Measurement) = measurementDao.insertMeasurement(measurement)

    suspend fun updateMeasurement(measurement: Measurement) = measurementDao.updateMeasurement(measurement)

    suspend fun deleteMeasurement(measurement: Measurement) = measurementDao.deleteMeasurement(measurement)
}