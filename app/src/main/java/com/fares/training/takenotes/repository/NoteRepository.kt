package com.fares.training.takenotes.repository

import android.app.Application
import com.fares.training.takenotes.data.local.LocallyDeletedNoteId
import com.fares.training.takenotes.data.local.Note
import com.fares.training.takenotes.data.local.NoteDao
import com.fares.training.takenotes.data.remote.NoteApi
import com.fares.training.takenotes.data.remote.requests.AccountRequest
import com.fares.training.takenotes.data.remote.requests.AddOwnerRequest
import com.fares.training.takenotes.data.remote.requests.DeleteNoteRequest
import com.fares.training.takenotes.utils.Resource
import com.fares.training.takenotes.utils.isInternetActive
import com.fares.training.takenotes.utils.networkBoundResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class NoteRepository @Inject constructor(
    private val noteDao: NoteDao,
    private val noteApi: NoteApi,
    private val context: Application
) {

    suspend fun insertNote(note: Note) = try {
        noteApi.addNote(note)
    } catch (e: Exception) {
        null
    }?.isSuccessful?.let {
        if (it) {
            noteDao.insertNote(note.apply { isSynced = true })
        } else {
            noteDao.insertNote(note)
        }
    }

    suspend fun insertNotes(notes: List<Note>) = notes.forEach { insertNote(it) }


    suspend fun deleteNote(noteID: String) {
        val response = try {
            noteApi.deleteNote(DeleteNoteRequest(noteID))
        } catch (e: Exception) {
            null
        }
        noteDao.deleteNoteById(noteID)

        if (response == null || !response.isSuccessful) {
            noteDao.insertLocallyDeletedNoteId(LocallyDeletedNoteId(noteID))
        } else {
            deleteLocallyDeletedNoteId(noteID)
        }
    }

    fun observeNoteById(noteID: String) = noteDao.observeNoteById(noteID)

    suspend fun deleteLocallyDeletedNoteId(deletedNoteId: String) =
        noteDao.deleteLocallyDeletedNoteId(deletedNoteId)


    suspend fun getNoteById(noteID: String, block: (Note?) -> Unit) =
        block(noteDao.getNoteById(noteID))


    private var notesResponse: Response<List<Note>>? = null

    private suspend fun syncNotes() {
        noteDao.getAllLocallyDeletedNoteIds().forEach {
            deleteNote(it.deletedNoteId)
        }

        noteDao.getAllUnsyncedNotes().forEach { note ->
            insertNote(note)
        }

        notesResponse = noteApi.getNotes()
        notesResponse?.body()?.let {
            noteDao.deleteAllNotes()
            insertNotes(it.onEach { note -> note.isSynced = true })
        }
    }


    suspend fun registerUser(email: String, password: String) = withContext(Dispatchers.IO) {
        val response = noteApi.registerRequest(AccountRequest(email, password))
        try {
            if (response.isSuccessful) {
                Resource.Success(response.body()?.message)

            } else {
                Resource.Error(response.body()?.message ?: response.message(), null)

            }
        } catch (e: Exception) {
            Resource.Error("Couldn't connect to the servers. Check your internet connection", null)
        }
    }

    suspend fun addOwnerToNote(ownerEmail: String, noteId: String) = withContext(Dispatchers.IO) {
        val response = noteApi.addOwnerToNote(AddOwnerRequest(ownerEmail, noteId))
        try {
            if (response.isSuccessful && response.body()?.isSuccessful!!) {
                Resource.Success(response.body()?.message)

            } else {
                Resource.Error(response.body()?.message ?: response.message(), null)

            }
        } catch (e: Exception) {
            Resource.Error("Couldn't connect to the servers. Check your internet connection", null)
        }
    }

    suspend fun loginUser(email: String, password: String) = withContext(Dispatchers.IO) {
        val response = noteApi.loginRequest(AccountRequest(email, password))
        try {
            if (response.isSuccessful && response.body()?.isSuccessful!!) {
                Resource.Success(response.body()?.message)

            } else {
                Resource.Error(response.body()?.message ?: response.message(), null)

            }
        } catch (e: Exception) {
            Resource.Error("Couldn't connect to the servers. Check your internet connection", null)
        }
    }

    val allNotes = networkBoundResource(
        query = {
            noteDao.getAllNotes()
        },
        fetch = {
            syncNotes()
            notesResponse
        },
        saveFetchedResult = { response ->
            response?.body()?.let {
                insertNotes(it.onEach { note -> note.isSynced = true })
            }

        },
        shouldFetch = {
            isInternetActive(context)
        }
    )


}