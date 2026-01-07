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

    @Query("""
WITH ordered AS (
    SELECT
        date,
        streakMaintained,
        LAG(date) OVER (ORDER BY date) AS prevDate
    FROM userDailyRecord
    WHERE date < :today
),
breaks AS (
    SELECT
        CASE
            WHEN streakMaintained = 0 THEN date
            WHEN prevDate IS NOT NULL 
                 AND date - prevDate > 1
                 THEN prevDate + 1
        END AS breakDate
    FROM ordered
)
SELECT MAX(breakDate)
FROM breaks
""")
    fun getLatestStreakBreak(today: LocalDate) : Flow<LocalDate?>

    @Query(  """
    SELECT EXISTS(
        SELECT 1
        FROM userDailyRecord
        WHERE date = :date
          AND streakMaintained = 1
    )
    """)
    fun wasStreakMaintained(date: LocalDate) : Flow<Boolean>

    @Query(  """
    DELETE FROM userDailyRecord
    """)
    fun deleteAllRecords()

    @Query("SELECT COUNT(*) FROM userDailyRecord")
    fun getRowCount(): Flow<Int>
}