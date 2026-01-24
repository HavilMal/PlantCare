package com.plantCare.plantcare.database

import kotlinx.coroutines.flow.Flow

interface NotesRepository {
    suspend fun createNote(plantId: Long, title: String, content: String): Long
    suspend fun deleteNote(noteId: Long)
    suspend fun editNoteContent(noteId: Long, plantId: Long, title: String, content: String)
    fun getPlantNoteFlow(plantId: Long): Flow<Note>
    fun getPlantNotesFlow(plantId: Long): Flow<List<Note>>
}

class NotesRepositoryImpl(
    private val notesDAO: NotesDAO
) : NotesRepository {
    override suspend fun createNote(plantId: Long, title: String, content: String): Long {
        return notesDAO.insertNote(
            Note(
                plant = plantId,
                title = title,
                note = content,
            )
        )
    }

    override suspend fun deleteNote(noteId: Long) {
        notesDAO.deleteNote(noteId)
    }

    override suspend fun editNoteContent(noteId: Long, plantId: Long, title: String, content: String) {
        notesDAO.editNoteContent(
            Note(
                id = noteId,
                plant = plantId,
                title = title,
                note = content,
            )
        )
    }

    override fun getPlantNoteFlow(plantId: Long): Flow<Note> {
        return notesDAO.getPlantNote(plantId)
    }

    override fun getPlantNotesFlow(plantId: Long): Flow<List<Note>> {
        return notesDAO.getPlantNotes(plantId)
    }
}