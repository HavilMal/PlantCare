package com.plantCare.plantcare.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.time.DayOfWeek
import java.time.LocalDate
data class PlantWateringBaseInfo(
    val plantId: Long,
    val isIndoor: Boolean,
    val wateringInterval: WateringInterval,
    val lastWateredByUserOn: LocalDate?,
    val plantedOn: LocalDate,
)
data class WateringDayEntity(
    val day: DayOfWeek
)
data class PlantWateringIntervalConnectedInfo(
    @Embedded
    val plant: PlantWateringBaseInfo,

    @Relation(
        entity = WateringSchedule::class,
        parentColumn = "plantId",
        entityColumn = "plant",
        projection = ["day"]
    )
    val wateringDays: List<WateringDayEntity>

)
data class PlantWateringInfo(
    val plantId: Long,
    val isIndoor: Boolean,
    val wateringInterval: WateringInterval,
    val wateringDays: Set<DayOfWeek>,
    val lastWateredByUserOn: LocalDate?,
    val plantedOn: LocalDate,
)

fun PlantWateringIntervalConnectedInfo.get(): PlantWateringInfo =
    PlantWateringInfo(
        plantId = plant.plantId,
        isIndoor = plant.isIndoor,
        wateringInterval = plant.wateringInterval,
        wateringDays = wateringDays.map { it.day }.toSet(),
        lastWateredByUserOn = plant.lastWateredByUserOn,
        plantedOn = plant.plantedOn
    )

@Dao
interface WateringDao {
//    @Query("""
//        SELECT
//            p.id AS plantId,
//            p.isIndoor AS isIndoor,
//            p.wateringInterval AS wateringInterval,
//            COALESCE(ws.day,'MONDAY') AS wateringDay,
//            COALESCE((SELECT MAX(date) FROM wateringHistory WHERE plant = p.id),p.plantedOn) AS lastWateredByUserOn,
//            p.plantedOn AS plantedOn
//        FROM plants p
//        LEFT JOIN wateringSchedule ws ON p.id = ws.plant
//    """)
//    fun getAllPlantsWateringInfo() : Flow<List<PlantWateringInfo>>

    @Transaction
    @Query("""
        SELECT
            p.id AS plantId,
            p.isIndoor AS isIndoor,
            p.wateringInterval AS wateringInterval,
            COALESCE(
                (SELECT MAX(date)
                 FROM wateringHistory
                 WHERE plant = p.id),
                p.plantedOn
            ) AS lastWateredByUserOn,
            p.plantedOn AS plantedOn
        FROM plants p
    """)
    fun getAllPlantsWateringInfo(): Flow<List<PlantWateringIntervalConnectedInfo>>

    @Query("SELECT EXISTS(SELECT 1 FROM wateringHistory WHERE date = :date)")
    fun anyPlantWateredByUser(date: LocalDate): Flow<Boolean>

    @Query("""
        SELECT DISTINCT date
        FROM wateringHistory
        WHERE date BETWEEN :from AND :to
        ORDER BY date
    """)
    fun wateringDays(from: LocalDate, to: LocalDate) : Flow<List<LocalDate>>


    @Insert
    suspend fun insertWateringEntry(wateringEntry: WateringEntry)
}