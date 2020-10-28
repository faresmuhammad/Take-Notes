package com.fares.training.takenotes.ui.notes

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.fares.training.takenotes.data.local.Note
import com.fares.training.takenotes.repository.NoteRepository
import com.fares.training.takenotes.utils.Event
import kotlinx.coroutines.launch

class NotesViewModel @ViewModelInject constructor(
    private val noteRepository: NoteRepository
) : ViewModel() {

    private val _forceUpdate = MutableLiveData<Boolean>(false)

    private val _allNotes = _forceUpdate.switchMap {
        noteRepository.allNotes.asLiveData(viewModelScope.coroutineContext)
    }.switchMap {
        MutableLiveData(Event(it))
    }


    val allNotes = _allNotes

    fun syncAllNotes() = _forceUpdate.postValue(true)


    fun insertNote(note: Note) = viewModelScope.launch {
        noteRepository.insertNote(note)
    }

    fun deleteLocallyDeletedNoteId(deletedNoteId: String) = viewModelScope.launch {
        noteRepository.deleteLocallyDeletedNoteId(deletedNoteId)
    }

    fun deleteNote(noteId: String) = viewModelScope.launch {
        noteRepository.deleteNote(noteId)
    }
}