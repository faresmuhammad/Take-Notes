package com.fares.training.takenotes.ui.note_details

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fares.training.takenotes.repository.NoteRepository
import com.fares.training.takenotes.utils.Event
import com.fares.training.takenotes.utils.Resource
import kotlinx.coroutines.launch

class NoteDetailsViewModel @ViewModelInject constructor(
    private val noteRepository: NoteRepository
) : ViewModel() {

    fun observeNoteById(noteId: String) = noteRepository.observeNoteById(noteId)

    private val _addOwnerStatus = MutableLiveData<Event<Resource<String>>>()
    val addOwnerStatus = _addOwnerStatus


    fun addOwnerToNote(ownerEmail: String, noteId: String) {
        _addOwnerStatus.postValue(Event(Resource.Loading(null)))
        if (ownerEmail.isEmpty() || noteId.isEmpty()) {
            _addOwnerStatus.postValue(Event(Resource.Error("Email can'ot be empty", null)))
            return
        }
        viewModelScope.launch {
            val result = noteRepository.addOwnerToNote(ownerEmail, noteId)
            _addOwnerStatus.postValue(Event(result))
        }
    }
}