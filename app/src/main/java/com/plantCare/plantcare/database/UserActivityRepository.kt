package com.plantCare.plantcare.database

import android.util.Log
import com.plantCare.plantcare.utils.DateUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class UserActivityRepository(
    val userActivityDao: UserActivityDao
) {
    suspend fun insertUserStreakRecord(date: LocalDate){
        userActivityDao.insertDailyRecord(UserDailyRecord(date,true))
    }
    suspend fun insertUserStreakRecord(){
        insertUserStreakRecord(DateUtil.localDateToday())
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
}