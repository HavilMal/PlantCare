package com.plantCare.plantcare.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.Database
import java.util.Date
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}

@Database(entities = [Plant::class, Notes::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao
}


@Entity(tableName = "plants")
data class Plant(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val species: String
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
data class Notes(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val plant: Int,
    val note: String
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
data class WateringHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val plant: Int,
    val date: Date
)