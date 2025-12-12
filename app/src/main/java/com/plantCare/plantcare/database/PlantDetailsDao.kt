package com.plantCare.plantcare.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDetailsDao {
    @Query("SELECT * FROM plantDetails WHERE id = :plantId")
    fun getDetails(plantId: Long): Flow<PlantDetails?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateDetails(details: PlantDetails)
}