package com.fares.training.takenotes.ui.add_edit_note

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fares.training.takenotes.data.local.Note
import com.fares.training.takenotes.repository.NoteRepository
import com.fares.training.takenotes.util.Event
import com.fares.training.takenotes.util.Resource
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AddEditNoteViewModel @ViewModelInject constructor(
    private val noteRepository: NoteRepository
) : ViewModel() {

    private val _note = MutableLiveData<Event<Resource<Note>>>()
    val note: LiveData<Event<Resource<Note>>> = _note

    fun insertNote(note: Note) = GlobalScope.launch {
        noteRepository.insertNote(note)
    }

    fun getNoteById(noteId: String) = viewModelScope.launch {
        _note.postValue(Event(Resource.Loading(null)))
        noteRepository.getNoteById(noteId) { note ->
            note?.let {
                _note.postValue(Event(Resource.Success(it)))
            } ?: _note.postValue(Event(Resource.Error("Note not found", null)))

        }
    }
}