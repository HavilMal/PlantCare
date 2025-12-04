package com.plantCare.plantcare.database

import kotlinx.coroutines.flow.Flow


class NotesRepository(
    private val notesDAO: NotesDAO
) {
    suspend fun createNote(plantId: Long, title: String, content: String): Long {
        return notesDAO.insertNote(
            Note(
                plant = plantId,
                title = title,
                note = content,
            )
        )
    }

    suspend fun deleteNote(noteId: Long) {
        notesDAO.deleteNote(noteId)
    }

    suspend fun editNoteContent(noteId: Long, plantId: Long, title: String, content: String) {
        notesDAO.editNoteContent(
            Note(
                id = noteId,
                plant = plantId,
                title = title,
                note = content,
            )
        )
    }

    fun getPlantNoteFlow(plantId: Long): Flow<Note> {
        return notesDAO.getPlantNote(plantId)
    }

    fun getPlantNotesFlow(plantId: Long): Flow<List<Note>> {
        return notesDAO.getPlantNotes(plantId)
    }
}