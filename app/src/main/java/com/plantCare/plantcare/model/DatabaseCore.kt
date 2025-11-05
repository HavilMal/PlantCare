package com.plantCare.plantcare.model

import android.content.Context
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
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.Date

//class Converters {
//    @TypeConverter
//    fun timestampToDate(value: Long?): Date? {
//        return value?.let { Date(it) }
//    }
//
//    @TypeConverter
//    fun dateToTimestamp(date: Date?): Long? {
//        return date?.time
//    }
//}
//
//
//
//enum class WateringSchedule{
//    WEEKLY,
//    MONTHLY
//}
//@Entity(tableName = "plants")
//data class Plant(
//    @PrimaryKey(autoGenerate = true)
//    val id: Int = 0,
//    val name: String,
//    val species: String,
//    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
//    val plantedOn: Date = Date(),
//    @ColumnInfo(defaultValue = "MONTHLY")
//    val wateringSchedule: WateringSchedule = WateringSchedule.MONTHLY
//)
//
//@Entity(
//    tableName = "notes",
//    foreignKeys = [
//        ForeignKey(
//            entity = Plant::class,
//            parentColumns = ["id"],
//            childColumns = ["plant"],
//            onDelete = ForeignKey.NO_ACTION
//        )
//    ]
//)
//data class Note(
//    @PrimaryKey(autoGenerate = true)
//    val id: Int = 0,
//    val plant: Int,
//    val note: String
//)
//
//
//
//@Entity(
//    tableName = "wateringHistory",
//    foreignKeys = [
//        ForeignKey(
//            entity = Plant::class,
//            parentColumns = ["id"],
//            childColumns = ["plant"],
//            onDelete = ForeignKey.NO_ACTION
//        )
//    ]
//)
//data class WateringEntry(
//    val plant: Int,
//    val date: Date
//)
//
//enum class NotificationSetting{
//    ALWAYS_NOTIFY,
//    NEVER
//}
//
//@Entity(
//    tableName = "userSettings"
//)
//data class UserSettings(
//    @ColumnInfo(defaultValue = "'DD/MM/YYYY'")
//    val dateFormat: String = "DD/MM/YYYY",
//    @ColumnInfo(defaultValue = "")
//    val location: String = "",
//    @ColumnInfo(defaultValue = "'ALWAYS_NOTIFY'")
//    val notification: NotificationSetting = NotificationSetting.ALWAYS_NOTIFY
//)


//@Dao
//interface PlantDao{
//    @Query("SELECT * FROM plants")
//    fun getPlants(): Flow<List<Plant>>
//    @Query("SELECT note FROM notes WHERE plant = :plantId")
//    fun getPlantNotes(plantId: Long): Flow<List<String>>
//    @Query("SELECT date FROM wateringHistory WHERE plant = :plantId")
//    fun getPlantWateringHistory(plantId: Long): Flow<List<Plant>>
//    @Insert
//    suspend fun insertPlant(plant: Plant): Long
//    @Insert
//    suspend fun insertNote(note: Note): Long
//    @Insert
//    suspend fun insertWateringEntry(wateringEntry: WateringEntry): Long
//    @Delete
//    suspend fun deletePlant(plant: Plant)
//    @Delete
//    suspend fun deleteNote(note: Note)
//    @Update
//    suspend fun updatePlant(plant: Plant)
//    @Update
//    suspend fun updateNote(note: Note)
//}
//@Dao
//interface UserSettingsDao{
//    @Update
//    suspend fun updateUserSettings(userSettings: UserSettings)
//}

//@Database(entities = [Plant::class, Note::class, WateringEntry::class ,UserSettings::class], version = 1)
//@TypeConverters(Converters::class)
//abstract class AppDatabase : RoomDatabase() {
//    abstract fun plantDao(): PlantDao
//    abstract fun userSettingsDao(): UserSettings
//
//    companion object {
//        @Volatile
//        private var instance: AppDatabase? = null
//        private val LOCK = Any()
//        fun getInstance(context: Context) : AppDatabase{
//            if(instance == null)
//                synchronized(LOCK) {
//                    instance = Room.databaseBuilder(
//                        context.applicationContext,
//                        AppDatabase::class.java,
//                        "your_db_name"
//                    ).allowMainThreadQueries() //not recommended though but you mention simple app so !
//                        .build()
//                }
//
//            return instance!!
//        }
//    }
//}
//


@Entity(tableName = "testTable")
data class TestTable(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String
)

@Dao
interface TestDao{
    @Insert
    suspend fun insertTest(testTable: TestTable)
}

@Database(entities = [TestTable::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun testDao(): TestDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null
        private val LOCK = Any()

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(LOCK) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "your_db_name"
                ).build().also { instance = it }
            }
        }
    }
}