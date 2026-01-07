package com.plantCare.plantcare.database

import com.plantCare.plantcare.utils.DateUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate


class WateringRepository(
    private val wateringDao: WateringDao
) {
    fun getAllPlantsWateringInfo(): Flow<List<PlantWateringInfo>> {
        return wateringDao.getAllPlantsWateringInfo().map { list -> list.map { it.get() } }
    }

    suspend fun insertWateringEntry(plantId: Long){
        wateringDao.insertWateringEntry(WateringEntry(plantId, DateUtil.localDateToday()))
    }

    fun anyPlantWateredByUser(date: LocalDate) : Flow<Boolean> {
        return wateringDao.anyPlantWateredByUser(date)
    }

    fun wateringDays(from: LocalDate, to: LocalDate) : Flow<List<LocalDate>> {
        return wateringDao.wateringDays(from,to)
    }
}