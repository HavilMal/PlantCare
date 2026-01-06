package com.plantCare.plantcare.logic

import android.util.Log
import com.plantCare.plantcare.database.Plant
import com.plantCare.plantcare.database.PlantRepository
import com.plantCare.plantcare.database.WateringDao
import com.plantCare.plantcare.database.WateringRepository
import com.plantCare.plantcare.database.WeatherRecord
import com.plantCare.plantcare.database.WeatherRepository
import com.plantCare.plantcare.utils.RandomUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Date

enum class WateringStatus { // today's status
    NEEDS_NO_WATERING,
    NEEDS_WATERING,
    WATERED_BY_WEATHER,
    WATERED_BY_USER,
    WATERED_BY_USER_AND_WEATHER
}

data class PlantWateringStatus(
    val plantId: Long,
    val status: WateringStatus
)

class WateringLogicEvaluator(
    private val weatherRepository: WeatherRepository,
    private val wateringRepository: WateringRepository
){
    fun getPlantsWaterStatus(date: LocalDate) : Flow<List<PlantWateringStatus>> {
        val plantsFlow = wateringRepository.getAllPlantsWateringInfo()

        return plantsFlow.map { plantInfos ->
            plantInfos.mapNotNull { info ->
                val baseStatus = when {
                    date == info.lastWateredByUserOn ->
                        WateringStatus.WATERED_BY_USER

                    ChronoUnit.DAYS.between(info.lastWateredByUserOn, date) >= info.wateringInterval.interval &&
                            info.wateringDays.contains(date.dayOfWeek) ->
                        WateringStatus.NEEDS_WATERING

                    else ->
                        WateringStatus.NEEDS_NO_WATERING
                }

                val finalStatus = if (!info.isIndoor) {
                    val rained : Boolean = weatherRepository.hasRainedOn(date)

                    when {
                        rained ->
                            when {
                                baseStatus == WateringStatus.WATERED_BY_USER -> WateringStatus.WATERED_BY_USER_AND_WEATHER
                                else -> WateringStatus.WATERED_BY_WEATHER
                            }
                        else ->baseStatus
                    }
                } else baseStatus

                PlantWateringStatus(info.plantId, finalStatus)
            }
        }
    }
    fun getPlantsWaterStatusToday() : Flow<List<PlantWateringStatus>> {
        return getPlantsWaterStatus(LocalDate.now())
    }
}