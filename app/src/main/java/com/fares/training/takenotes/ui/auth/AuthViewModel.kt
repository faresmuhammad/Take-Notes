package com.fares.training.takenotes.ui.auth

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fares.training.takenotes.repository.NoteRepository
import com.fares.training.takenotes.util.Resource
import kotlinx.coroutines.launch

class AuthViewModel @ViewModelInject constructor(
    private val noteRepository: NoteRepository
) : ViewModel() {


    private val _registerStatus = MutableLiveData<Resource<String>>()
    val registerStatus: LiveData<Resource<String>> = _registerStatus

    private val _loginStatus = MutableLiveData<Resource<String>>()
    val loginStatus: LiveData<Resource<String>> = _loginStatus

    fun register(email: String, password: String, repeatedPassword: String) {
        _registerStatus.postValue(Resource.Loading(null))
        if (email.isEmpty() || password.isEmpty() || repeatedPassword.isEmpty()) {
            _registerStatus.postValue(Resource.Error("Please fill out all the fields",null))
            return
        }
        if (password != repeatedPassword) {
            _registerStatus.postValue(Resource.Error("The passwords don't match",null))
            return
        }
        viewModelScope.launch {

            val result = noteRepository.registerUser(email, password)
            _registerStatus.postValue(result)
        }
    }

    fun login(email: String, password: String) {
        _loginStatus.postValue(Resource.Loading(null))
        if (email.isEmpty() || password.isEmpty()) {
            _loginStatus.postValue(Resource.Error("Please fill out all the fields",null))
            return
        }

        viewModelScope.launch {
            val result = noteRepository.loginUser(email, password)
            _loginStatus.postValue(result)
        }
    }
}