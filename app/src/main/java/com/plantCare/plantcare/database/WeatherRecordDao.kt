package com.plantCare.plantcare.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.plantCare.plantcare.utils.DateUtil
import kotlinx.coroutines.flow.Flow
import java.sql.Timestamp
import java.time.LocalDate

@Dao
interface WeatherRecordDao {

    @Query("SELECT * FROM weatherRecord WHERE timestamp BETWEEN :from AND :to ORDER BY timestamp")
    fun getRecordsInRange(from: Timestamp, to: Timestamp) : List<WeatherRecord>

    @Query("SELECT COALESCE(SUM(rainVolume), 0) FROM weatherRecord WHERE timestamp BETWEEN :from AND :to")
    fun getTotalRainVolumeInRange(from: Timestamp,to: Timestamp) : Double

    @Query("""
        SELECT EXISTS(
            SELECT 1 
            FROM weatherRecord 
            WHERE timestamp BETWEEN :from AND :to
            AND hasRained = 1 )
        """)
    fun hasRainedInRange(from: Timestamp,to: Timestamp) : Boolean
    fun hasRainedInRange(from: LocalDate, to: LocalDate): Boolean {
        return hasRainedInRange(
            DateUtil.startOfDay(from),
            DateUtil.endOfDay(to)
        )
    }

    @Query("""
    SELECT EXISTS(
        SELECT 1 
        FROM weatherRecord
        WHERE timestamp BETWEEN :dayStart AND :dayEnd
        AND hasRained = 1
    )
""")
    suspend fun hasRainedOn(dayStart: Timestamp, dayEnd: Timestamp) : Boolean

    suspend fun hasRainedOn(date: LocalDate): Boolean {
        return hasRainedOn(
            DateUtil.startOfDay(date),
            DateUtil.endOfDay(date)
        )
    }

    @Query("""
    SELECT DISTINCT date(timestamp / 1000, 'unixepoch') AS rainDay
    FROM weatherRecord
    WHERE timestamp BETWEEN :from AND :to
      AND hasRained = 1
    ORDER BY rainDay
""")
    fun rainDays(from: LocalDate, to:LocalDate) : Flow<List<LocalDate>>

    @Query("""
    SELECT date(timestamp / 1000, 'unixepoch')
    FROM weatherRecord
    WHERE hasRained = 1
    ORDER BY timestamp DESC
    LIMIT 1
""")
    fun latestRainDay() : Flow<LocalDate?>


    @Query(
        """
        SELECT 
            COUNT(DISTINCT date(timestamp / 1000, 'unixepoch')) >= :dayCount
        FROM weatherRecord
        WHERE date(timestamp / 1000, 'unixepoch') 
              BETWEEN date('now') 
              AND date('now', '+' || (:dayCount - 1) || ' days')
        """
    )
    suspend fun nextDaysArchived(dayCount: Int) : Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecord(weatherRecord: WeatherRecord)

    @Query("SELECT * FROM weatherRecord")
    fun getAllRecords() : List<WeatherRecord>

    @Delete
    fun deleteRecord(weatherRecord: WeatherRecord)

    @Query("DELETE FROM weatherRecord WHERE timestamp < :threshold")
    fun deleteRecordsBefore(threshold: Timestamp)

    @Query("DELETE FROM weatherRecord")
    fun deleteAllRecords()
}