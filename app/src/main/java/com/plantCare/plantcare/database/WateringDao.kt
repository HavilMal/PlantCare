package com.plantCare.plantcare.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.time.DayOfWeek
import java.time.LocalDate

data class PlantWateringInfo(
    val plantId: Long,
    val isIndoor: Boolean,
    val wateringInterval: WateringInterval,
    val wateringDay: DayOfWeek?,
    val lastWateredOn: LocalDate? // watered by user
)
@Dao
interface WateringDao {
    @Query("""
        SELECT 
            p.id AS plantId,
            p.isIndoor AS isIndoor,
            p.wateringInterval AS wateringInterval,
            COALESCE(ws.day,'MONDAY') AS wateringDay,
            COALESCE((SELECT MAX(date) FROM wateringHistory WHERE plant = p.id),p.createdOn) AS lastWateredOn
        FROM plants p
        LEFT JOIN wateringSchedule ws ON p.id = ws.plant
    """)
    fun getAllPlantsWateringInfo() : Flow<List<PlantWateringInfo>>

    @Insert
    suspend fun insertWateringEntry(wateringEntry: WateringEntry)
}