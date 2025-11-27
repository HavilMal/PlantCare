package com.plantCare.plantcare.database

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
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
    val id: Int = 0,
    val plant: Int,
    val note: String
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
)


@Dao
interface PlantDao {
    @Query("SELECT * FROM plants")
    fun getPlants(): Flow<List<Plant>>

    @Query("SELECT * FROM plants WHERE id = :plantId")
    fun getPlant(plantId: Long): Flow<Plant>

    @Query("SELECT dirPath FROM plants WHERE id = :plantId")
    suspend fun getPlantDirPath(plantId: Long): String?

    @Query("SELECT note FROM notes WHERE plant = :plantId")
    fun getPlantNotes(plantId: Long): Flow<List<String>>

    @Query("SELECT date FROM wateringHistory WHERE plant = :plantId")
    fun getPlantWateringHistory(plantId: Long): Flow<List<LocalDate>>

    @Query("SELECT * FROM wateringSchedule WHERE plant = :plantId")
    fun getWateringSchedule(plantId: Long): Flow<List<WateringSchedule>>

    @Query("DELETE FROM plants")
    suspend fun deleteAllPlants()

    @Insert
    suspend fun insertWateringSchedule(schedule: WateringSchedule): Long

    @Query("DELETE FROM wateringSchedule WHERE plant = :plantId")
    suspend fun deleteScheduleForPlant(plantId: Long)

    @Insert
    suspend fun insertPlant(plant: Plant): Long

    @Insert
    suspend fun insertNote(note: Note): Long

    @Insert
    suspend fun insertWateringEntry(wateringEntry: WateringEntry): Long

    @Delete
    suspend fun deletePlant(plant: Plant)

    @Delete
    suspend fun deleteNote(note: Note)

    @Update
    suspend fun updatePlant(plant: Plant)

    @Update
    suspend fun updateNote(note: Note)

}

@Database(
    entities = [Plant::class, Note::class, WateringEntry::class, WateringSchedule::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao
}