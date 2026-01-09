package com.plantCare.plantcare.database

import com.plantCare.plantcare.ui.screens.calendarScreen.WateringState
import com.plantCare.plantcare.utils.DateUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit


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


    suspend fun wasWateredByUser(plantId: Long, date: LocalDate) : Boolean {
        return wateringDao.wasWatered(plantId,date)
    }

    suspend fun needsWatering(plantId: Long, date: LocalDate) : Boolean {
        val interval: WateringInterval = wateringDao.getWateringInterval(plantId)
        wateringDao.getWateringSchedule(plantId).forEach { schedule ->
            val diff = ChronoUnit.DAYS.between(schedule.startingDate, date)
            if (diff >= 0 && diff % interval.interval == 0L) {
                return true
            }
        }
        return false
    }
}