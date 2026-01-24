package com.plantCare.plantcare.repository

import com.plantCare.plantcare.database.Note
import com.plantCare.plantcare.database.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeNotesRepository : NotesRepository {
    private val notes = mutableListOf<Note>()

    override suspend fun createNote(
        plantId: Long,
        title: String,
        content: String
    ): Long {
        val id = notes.size.toLong() + 1L
        notes.add(Note(
            id = id,
            plant = plantId,
            title = title,
            note = content,
        ))

        return id
    }

    override suspend fun deleteNote(noteId: Long) {
        notes.removeAll { it.id == noteId }
    }

    override suspend fun editNoteContent(
        noteId: Long,
        plantId: Long,
        title: String,
        content: String
    ) {
        val index = notes.indexOfFirst { it.id == noteId }
        if (index != -1) {
            notes[index] = Note(id = noteId, plant = plantId, title = title, note = content)
        }
    }

    override fun getPlantNoteFlow(plantId: Long): Flow<Note> = flow {
        emit(Note(1, 1, "title ", "content"))
    }

    override fun getPlantNotesFlow(plantId: Long): Flow<List<Note>> = flow {
        emit(listOf(Note(1, 1, "title ", "content")))
    }

}
