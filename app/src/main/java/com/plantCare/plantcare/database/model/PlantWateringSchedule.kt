package com.plantCare.plantcare.database.model

import com.plantCare.plantcare.database.WateringInterval
import java.time.DayOfWeek
import java.time.LocalDate

class PlantWateringSchedule (
    val plant: Long,
    val day: DayOfWeek,
    val startingDate: LocalDate,
    val wateringInterval: WateringInterval,
)