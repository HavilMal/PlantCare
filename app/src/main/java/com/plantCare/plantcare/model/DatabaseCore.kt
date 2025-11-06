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

@Entity
data class User(
    @PrimaryKey val uid: Int,
    @ColumnInfo(name = "first_name") val firstName: String?,
    @ColumnInfo(name = "last_name") val lastName: String?
)

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAll(): List<User>

    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<User>

    @Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
           "last_name LIKE :last LIMIT 1")
    fun findByName(first: String, last: String): User

    @Insert
    fun insertAll(vararg users: User)

    @Delete
    fun delete(user: User)
}

@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        private val DATABASE_NAME = "plant_care_db"
        /*The value of a volatile variable will never be cached, and all writes and reads will be done to and from the main memory.
        This helps make sure the value of INSTANCE is always up-to-date and the same for all execution threads.
        It means that changes made by one thread to INSTANCE are visible to all other threads immediately.*/
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase{
            // only one thread of execution at a time can enter this block of code
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                                        context = context.applicationContext,
                                        klass = AppDatabase::class.java,
                                        name = DATABASE_NAME
                                    )
//                        .fallbackToDestructiveMigration(false)
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}