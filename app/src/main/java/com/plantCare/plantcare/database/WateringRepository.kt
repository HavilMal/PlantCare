package com.plantCare.plantcare.database

import com.plantCare.plantcare.utils.DateUtil
import kotlinx.coroutines.flow.Flow


class WateringRepository(
    private val wateringDao: WateringDao
) {
    fun getAllPlantsWateringInfo(): Flow<List<PlantWateringInfo>> {
        return wateringDao.getAllPlantsWateringInfo()
    }

    suspend fun insertWateringEntry(plantId: Long){
        wateringDao.insertWateringEntry(WateringEntry(plantId, DateUtil.localDateToday()))
    }
}