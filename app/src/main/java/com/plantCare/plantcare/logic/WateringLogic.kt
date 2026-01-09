package com.plantCare.plantcare.logic

import com.plantCare.plantcare.database.WateringRepository
import com.plantCare.plantcare.database.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.temporal.ChronoUnit

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

//                    ChronoUnit.DAYS.between(info.lastWateredByUserOn ?: info.plantedOn, date) >= info.wateringInterval.interval && info.wateringDays.contains(date.dayOfWeek) ->
//                    ChronoUnit.DAYS.between(info.plantedOn, date) >= info.wateringInterval.interval && info.wateringDays.contains(date.dayOfWeek) ->
                        wateringRepository.needsWatering(info.plantId,date) && info.lastWateredByUserOn != date ->
                        WateringStatus.NEEDS_WATERING

                    date == info.plantedOn ->
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