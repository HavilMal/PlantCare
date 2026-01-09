package com.plantCare.plantcare.database

import android.util.Log
import androidx.compose.material3.DatePicker
import com.plantCare.plantcare.utils.DateUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class UserActivityRepository(
    val userActivityDao: UserActivityDao,
    val weatherRepository: WeatherRepository,
    val wateringRepository: WateringRepository,
    val plantRepository: PlantRepository
) {
    suspend fun insertUserStreakRecord(streakMaintained: Boolean, date: LocalDate, ){
        userActivityDao.insertDailyRecord(UserDailyRecord(date,streakMaintained))
    }
    suspend fun insertUserStreakRecord(streakMaintained: Boolean){
        insertUserStreakRecord(streakMaintained,DateUtil.localDateToday())
    }

    fun todayStreakMaintained() : Flow<Boolean> {
        return userActivityDao.wasStreakMaintained(DateUtil.localDateToday())
    }
    suspend fun getUserCurrentStreak() : Flow<Int> {
        val today = DateUtil.localDateToday()
        val latestBreakFlow = userActivityDao.getLatestStreakBreak(today)
        val todayMaintainedFlow = todayStreakMaintained()
        val totalRowsFlow = userActivityDao.getRowCount()
        return latestBreakFlow.combine(todayMaintainedFlow) { latestBreak, todayMaintained ->
            latestBreak to todayMaintained
        }.combine(totalRowsFlow) { (latestBreak, todayMaintained), rowCount ->
             if (latestBreak == null) {
                rowCount
            } else {
                ChronoUnit.DAYS.between(latestBreak, today).toInt() - 1 + if (todayMaintained) 1 else 0
            }
        }
    }

    suspend fun deleteRecords(){
        userActivityDao.deleteAllRecords()
    }

    suspend fun updateUserStreakData() {
        val today: LocalDate = DateUtil.localDateToday()
        val plants: List<Plant> = plantRepository.getAllPlants()

        suspend fun streakBroke(plantId: Long, isIndoor: Boolean, currentDate: LocalDate): Boolean {
            if (wateringRepository.needsWatering(plantId, currentDate)) {
                if (!wateringRepository.wasWateredByUser(plantId, currentDate)) {
                    if (isIndoor) {
                        return true
                    } else if (!weatherRepository.hasRainedOn(currentDate)) {
                        return true
                    }
                }
            }
            return false
        }

        val latest: LocalDate? = userActivityDao.latestRecordedDate() ?: today
        var currentDate = today
        do {
            var streakMaintained = true

            for (plant in plants) {
                if (streakBroke(plant.id, plant.isIndoor, currentDate)) {
                    streakMaintained = false
                    break
                }
            }

            insertUserStreakRecord(streakMaintained, currentDate)
            currentDate = currentDate.minusDays(1)
        } while (currentDate.isAfter(latest))
    }
}