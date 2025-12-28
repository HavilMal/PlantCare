package com.plantCare.plantcare.database

import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Transaction
import androidx.room.Update
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.plantCare.plantcare.database.model.PlantWateringSchedule
import kotlinx.coroutines.flow.Flow
import java.lang.reflect.Type
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

    @TypeConverter
    fun stringToArrayList(value: String): List<String> {
        val listType: Type = object : TypeToken<ArrayList<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun arrayListToString(list: List<String>): String {
        return Gson().toJson(list)
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
    val wateringInterval: WateringInterval,
    val apiId: Long?,
    val sensorAddress: String?,
)

@Entity(
    tableName = "plantDetails",
    foreignKeys = [
        ForeignKey(
            entity = Plant::class,
            parentColumns = ["id"],
            childColumns = ["id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
// fruits / harvest
data class PlantDetails(
    @PrimaryKey(autoGenerate = false)
    val id: Long,
    val updatedOn: LocalDate,
    val commonName: String,
    val scientificName: String,
    val wateringValue: String,
    val wateringUnit: String,
    val sunlight: List<String>,
    val pruningMonths: List<String>,
    val soil: List<String>,
    val description: String,
)

@Entity(
    tableName = "notes",
    foreignKeys = [
        ForeignKey(
            entity = Plant::class,
            parentColumns = ["id"],
            childColumns = ["plant"],
            onDelete = ForeignKey.CASCADE
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
    tableName = "plantMedia",
    primaryKeys = ["plant", "media"],
    foreignKeys = [
        ForeignKey(
            entity = Plant::class,
            parentColumns = ["id"],
            childColumns = ["plant"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PlantMedia(
    val plant: Long,
    val media: String // name of the file in media/
)

@Entity(
    tableName = "wateringHistory",
    primaryKeys = ["plant", "date"],
    foreignKeys = [
        ForeignKey(
            entity = Plant::class,
            parentColumns = ["id"],
            childColumns = ["plant"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["plant"])
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
    fun getPlantFlow(plantId: Long): Flow<Plant?>

    @Query("SELECT media FROM plantMedia WHERE plant = :plantId")
    fun getPlantMediaFlow(plantId: Long): Flow<List<String>>
    @Query("SELECT date FROM wateringHistory WHERE plant = :plantId")
    fun getPlantWateringHistory(plantId: Long): Flow<List<LocalDate>>

    @Query("SELECT * FROM wateringSchedule WHERE plant = :plantId")
    fun getWateringSchedule(plantId: Long): Flow<List<WateringSchedule>>

    @Query("DELETE FROM plants")
    suspend fun deleteAllPlants()
    @Query("DELETE FROM plantMedia WHERE media = :media")
    suspend fun deletePlantMedia(media: String)

    @Query("SELECT * FROM wateringSchedule")
    fun getWateringSchedules(): Flow<List<WateringSchedule>>

    @Query("SELECT apiId FROM plants WHERE id = :plantId")
    fun getApiId(plantId: Long): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWateringSchedule(schedule: WateringSchedule): Long

    @Query("DELETE FROM wateringSchedule WHERE plant = :plantId")
    suspend fun deleteScheduleForPlant(plantId: Long)

    @Query(
        """
        SELECT ws1.plant, ws1.day, ws1.startingDate, p1.wateringInterval
        FROM wateringSchedule ws1 
        INNER JOIN plants p1 ON ws1.plant = p1.id
         """
    )
    fun getPlantWateringSchedules(): Flow<List<PlantWateringSchedule>>

    @Query("UPDATE plants SET sensorAddress = :address WHERE id = :plantId")
    suspend fun updateSensorAddress(plantId: Long, address: String?)
    @Insert
    suspend fun insertPlant(plant: Plant): Long
    @Insert
    suspend fun insertPlantMedia(plantMedia: PlantMedia)
    @Query("INSERT INTO plantMedia(plant, media) VALUES (:plantId, :mediaName)")
    suspend fun insertPlantMedia(plantId: Long, mediaName: String)

    @Insert
    suspend fun insertWateringEntry(wateringEntry: WateringEntry): Long

    @Delete
    suspend fun deletePlant(plant: Plant)
    @Delete
    suspend fun deletePlantMedia(plantMedia: PlantMedia)

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
    entities = [
        Plant::class,
        Note::class,
        WateringEntry::class,
        WateringSchedule::class,
        PlantDetails::class,
        PlantMedia::class
    ],
    version = 1
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao

    abstract fun notesDAO(): NotesDAO

    abstract fun plantDetailsDAO(): PlantDetailsDao

}