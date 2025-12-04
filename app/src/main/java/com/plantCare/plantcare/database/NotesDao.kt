package com.plantCare.plantcare.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDAO {
    @Query("SELECT * FROM notes WHERE plant = :plantId")
    fun getPlantNotes(plantId: Long): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE id = :noteId")
    fun getPlantNote(noteId: Long): Flow<Note>

    @Insert
    suspend fun insertNote(note: Note): Long

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNote(id: Long)

    @Update
    suspend fun editNoteContent(note: Note)
}