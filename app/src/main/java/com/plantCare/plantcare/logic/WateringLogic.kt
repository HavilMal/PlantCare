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

    suspend fun getPlantsWaterStatusToday() : Flow<List<PlantWateringStatus>> {
        val today : LocalDate = LocalDate.now()
//        Log.d("devo","0")
//        val whatever = wateringRepository.getAllPlantsWateringInfo()
//        Log.d("devo","1 whatever empty? = ${whatever.firstOrNull()}")
//        val whatever2 = wateringRepository.getAllPlantsWateringInfo().first()
//        Log.d("devo","3")
//        if(whatever2 != null && !whatever2.isEmpty()) Log.d("devo",whatever.first().toString())
//        Log.d("devo","2")
        return wateringRepository.getAllPlantsWateringInfo().map { infos ->
                infos.mapNotNull { info ->
                    when {
                        info.isIndoor ->
                            when {
                                today == info.lastWateredOn -> // user watered today
                                    PlantWateringStatus(info.plantId, WateringStatus.WATERED_BY_USER)

                                ChronoUnit.DAYS.between( // hasnt been watered yet
                                    info.lastWateredOn,
                                    today
                                ) >= info.wateringInterval.interval ->
                                    PlantWateringStatus(info.plantId, WateringStatus.NEEDS_WATERING)

                                else -> // interval not reached yet
                                    PlantWateringStatus(info.plantId, WateringStatus.NEEDS_NO_WATERING)
                            }
                        else ->
                            when {
                                ChronoUnit.DAYS.between(
                                    info.lastWateredOn,
                                    today
                                ) >= info.wateringInterval.interval ->
                                    when {
                                        weatherRepository.hasRainedOn(today)->  // weather watered today
                                            when {
                                                today == info.lastWateredOn ->
                                                    PlantWateringStatus(info.plantId, WateringStatus.WATERED_BY_USER_AND_WEATHER)
                                                else ->
                                                    PlantWateringStatus(info.plantId, WateringStatus.WATERED_BY_WEATHER)
                                            }

                                        today == info.lastWateredOn ->
                                            PlantWateringStatus(info.plantId, WateringStatus.WATERED_BY_USER)

                                        else ->
                                            PlantWateringStatus(info.plantId, WateringStatus.NEEDS_WATERING)
                                    }

                                else ->
                                    PlantWateringStatus(info.plantId, WateringStatus.NEEDS_NO_WATERING)
                            }
                    }
                }
            }
    }
}