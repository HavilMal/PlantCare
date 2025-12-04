package com.plantCare.plantcare.database

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Transaction
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.Update
import com.plantCare.plantcare.database.model.PlantWateringSchedule
import kotlinx.coroutines.flow.Flow
import java.time.DayOfWeek
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun timestampToDate(value: Long?): LocalDate? {
        return value?.let { LocalDate.ofEpochDay(value) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): Long? {
        return date?.toEpochDay()
    }
}


@Entity(tableName = "plants")
data class Plant(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String,
    val isIndoor: Boolean,
    val species: String,
    val plantedOn: LocalDate,
    val createdOn: LocalDate,
    @ColumnInfo(defaultValue = "MONTHLY")
    val dirPath: String,
    val wateringInterval: WateringInterval,
)

@Entity(
    tableName = "notes",
    foreignKeys = [
        ForeignKey(
            entity = Plant::class,
            parentColumns = ["id"],
            childColumns = ["plant"],
            onDelete = ForeignKey.NO_ACTION
        )
    ]
)
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val plant: Long,
    val title: String,
    val note: String,
)


@Entity(
    tableName = "wateringHistory",
    primaryKeys = ["plant", "date"],
    foreignKeys = [
        ForeignKey(
            entity = Plant::class,
            parentColumns = ["id"],
            childColumns = ["plant"],
            onDelete = ForeignKey.NO_ACTION
        )
    ]
)
data class WateringEntry(
    val plant: Int,
    val date: LocalDate
)

enum class WateringInterval(
    val label: String,
    val interval: Int,
) {
    WEEK("Weekly", 7),
    TWO_WEEKS("Two weeks", 14),
    THREE_WEEKS("Three weeks", 21),
    FOUR_WEEKS("Four Weeks", 28),
}

@Entity(
    tableName = "wateringSchedule",
    primaryKeys = ["plant", "day"],
    foreignKeys = [
        ForeignKey(
            entity = Plant::class,
            parentColumns = ["id"],
            childColumns = ["plant"],
            onDelete = ForeignKey.CASCADE,
        )
    ]
)
data class WateringSchedule(
    val plant: Long,
    val day: DayOfWeek,
    val startingDate: LocalDate,
)


@Dao
interface PlantDao {
    @Query("SELECT * FROM plants")
    fun getPlants(): Flow<List<Plant>>
    @Query("SELECT * FROM plants WHERE id = :plantId")
    suspend fun getPlant(plantId: Long): Plant
    @Query("SELECT * FROM plants WHERE id = :plantId")
    fun getPlantFlow(plantId: Long): Flow<Plant>

    @Query("SELECT dirPath FROM plants WHERE id = :plantId")
    suspend fun getPlantDirPath(plantId: Long): String?

    @Query("SELECT date FROM wateringHistory WHERE plant = :plantId")
    fun getPlantWateringHistory(plantId: Long): Flow<List<LocalDate>>

    @Query("SELECT * FROM wateringSchedule WHERE plant = :plantId")
    fun getWateringSchedule(plantId: Long): Flow<List<WateringSchedule>>

    @Query("DELETE FROM plants")
    suspend fun deleteAllPlants()

    @Query("SELECT * FROM wateringSchedule")
    fun getWateringSchedules(): Flow<List<WateringSchedule>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWateringSchedule(schedule: WateringSchedule): Long

    @Query("DELETE FROM wateringSchedule WHERE plant = :plantId")
    suspend fun deleteScheduleForPlant(plantId: Long)

    @Query("""
        SELECT ws1.plant, ws1.day, ws1.startingDate, p1.wateringInterval
        FROM wateringSchedule ws1 
        INNER JOIN plants p1 ON ws1.plant = p1.id
         """)
    fun getPlantWateringSchedules(): Flow<List<PlantWateringSchedule>>

    @Insert
    suspend fun insertPlant(plant: Plant): Long

    @Insert
    suspend fun insertWateringEntry(wateringEntry: WateringEntry): Long

    @Delete
    suspend fun deletePlant(plant: Plant)

    @Update
    suspend fun updatePlant(plant: Plant)

    @Update
    suspend fun updateNote(note: Note)

    @Transaction
    suspend fun setSchedule(plantId: Long, days: Set<DayOfWeek>, interval: WateringInterval) {
        val plant: Plant = getPlant(plantId)
        updatePlant(plant.copy(wateringInterval = interval))
        deleteScheduleForPlant(plantId)
        days.forEach { it ->
            val startingDate = plant.createdOn.with(it)
            insertWateringSchedule(WateringSchedule(plantId, it, startingDate))
        }
    }
}

@Database(
    entities = [Plant::class, Note::class, WateringEntry::class, WateringSchedule::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao

    abstract fun notesDAO(): NotesDAO
}