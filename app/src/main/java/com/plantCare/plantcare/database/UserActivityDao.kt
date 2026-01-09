package com.plantCare.plantcare.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate


@Dao
interface UserActivityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyRecord(userDailyRecord: UserDailyRecord)

    @Query(
        """
        WITH ordered AS (
            SELECT
                date,
                streakStatus,
                LAG(date) OVER (ORDER BY date) AS prevDate
            FROM userDailyRecord
            WHERE date < :today
        ),
        breaks AS (
            SELECT
                CASE
                    WHEN streakStatus = 'NEGATIVE' THEN date
                    WHEN prevDate IS NOT NULL
                         AND date - prevDate > 1
                         THEN prevDate + 1
                END AS breakDate
            FROM ordered
        )
        SELECT MAX(breakDate)
        FROM breaks
        """
    )
    fun getLatestStreakBreak(today: LocalDate) : Flow<LocalDate?>

    @Query("SELECT MAX(date) FROM userDailyRecord")
    suspend fun latestRecordedDate() : LocalDate?

    @Query("SELECT date FROM userDailyRecord WHERE streakStatus = 'NEGATIVE' AND date BETWEEN :from AND :to")
    fun getStreakBreaks(from:LocalDate, to: LocalDate) : Flow<List<LocalDate>>

    @Query(  """
    SELECT EXISTS(
        SELECT 1
        FROM userDailyRecord
        WHERE date = :date
          AND streakStatus = 'POSITIVE'
    )
    """)
    fun wasStreakMaintained(date: LocalDate) : Flow<Boolean>

    @Query(  """
    DELETE FROM userDailyRecord
    """)
    fun deleteAllRecords()

    @Query("SELECT COUNT(*) FROM userDailyRecord")
    fun getRowCount(): Flow<Int>
    @Query(
        """
        SELECT COUNT(*)
        FROM userDailyRecord
        WHERE streakStatus = 'POSITIVE'
        """
    )
    fun getPositiveRowCount(): Flow<Int>

    @Query(
        """
        SELECT COUNT(*)
        FROM userDailyRecord
        WHERE streakStatus = 'POSITIVE'
          AND date BETWEEN :startDate AND :endDate
        """
    )
    fun getPositiveRowCount(
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<Int>

    @Query("SELECT * FROM userDailyRecord")
    suspend fun getAllRecords() : List<UserDailyRecord>
}