package com.plantCare.plantcare.database

import com.plantCare.plantcare.utils.DateUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class UserActivityRepository(
    val userActivityDao: UserActivityDao,
    val weatherRepository: WeatherRepository,
    val wateringRepository: WateringRepository,
    val plantRepository: PlantRepositoryImpl
) {
    suspend fun insertUserStreakRecord(streakStatus: StreakStatus, date: LocalDate){
        userActivityDao.insertDailyRecord(UserDailyRecord(date,streakStatus))
    }
    suspend fun insertUserStreakRecord(streakStatus: StreakStatus){
        insertUserStreakRecord(streakStatus,DateUtil.localDateToday())
    }
    fun todayStreakMaintained() : Flow<Boolean> {
        return userActivityDao.wasStreakMaintained(DateUtil.localDateToday())
    }
    fun getStreakBreaks(from:LocalDate, to: LocalDate): Flow<List<LocalDate>> {
        return userActivityDao.getStreakBreaks(from,to)
    }
    suspend fun getUserCurrentStreak() : Flow<Int> {
        val today = DateUtil.localDateToday()

        val latestBreakFlow = userActivityDao.getLatestStreakBreak(today)
        val todayMaintainedFlow = todayStreakMaintained()

        return latestBreakFlow
            .combine(todayMaintainedFlow) { latestBreak, todayMaintained ->
                latestBreak to todayMaintained
            }
            .flatMapLatest { (latestBreak, todayMaintained) ->
                if (latestBreak == null) {
                    userActivityDao.getPositiveRowCount()
                } else {
                    userActivityDao
                        .getPositiveRowCount(latestBreak, today)
                        .map { positiveCount ->
//                            positiveCount + if (todayMaintained) 1 else 0
                            positiveCount
                        }
                }
            }
    }

    suspend fun deleteRecords(){
        userActivityDao.deleteAllRecords()
    }
    suspend fun getAllRecords() : List<UserDailyRecord>{
        return userActivityDao.getAllRecords()
    }

    suspend fun updateUserStreakData() {
        val today: LocalDate = DateUtil.localDateToday()
        val plants: List<Plant> = plantRepository.getAllPlants()
        if(plants.isEmpty()){
            insertUserStreakRecord(StreakStatus.NEUTRAL,today)
            return
        }
        suspend fun streakEvaluation(plantId: Long, isIndoor: Boolean, currentDate: LocalDate): StreakStatus {
            if (wateringRepository.needsWatering(plantId, currentDate)) {
                if (!wateringRepository.wasWateredByUser(plantId, currentDate)) {
                    if (isIndoor) {
                        return StreakStatus.NEGATIVE
                    } else {
                        if (!weatherRepository.hasRainedOn(currentDate)) {
                            return StreakStatus.NEGATIVE
                        }
                        else {
                            return StreakStatus.POSITIVE
                        }
                    }

                } else {
                    return StreakStatus.POSITIVE
                }
            } else {
                return StreakStatus.NEUTRAL
            }
        }

        val latest: LocalDate? = userActivityDao.latestRecordedDate() ?: today
        var currentDate = today
        do {
            var nonNegative = true
            var anyWatered = false
            for (plant in plants) {
                val evaluation = streakEvaluation(plant.id, plant.isIndoor, currentDate)
                if (evaluation == StreakStatus.NEGATIVE) {
                    nonNegative = false
                } else {
                    if (evaluation == StreakStatus.POSITIVE){
                        anyWatered = true
                    }
                }
            }

            val final: StreakStatus =
                if(!nonNegative){
                    StreakStatus.NEGATIVE
                } else {
                    if(anyWatered){
                        StreakStatus.POSITIVE
                    } else {
                        StreakStatus.NEUTRAL
                    }
                }

            insertUserStreakRecord(final, currentDate)
            currentDate = currentDate.minusDays(1)
        } while (currentDate.isAfter(latest))
    }
}