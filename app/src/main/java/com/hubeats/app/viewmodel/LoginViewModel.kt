package com.hubeats.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hubeats.app.data.entity.User
import com.hubeats.app.data.repository.HubEatsRepository
import kotlinx.coroutines.launch

class LoginViewModel( private val repository: HubEatsRepository ) : ViewModel() {

    private val _loginResult = MutableLiveData<User?>()
    val loginResult : LiveData<User?> = _loginResult

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage : LiveData<String?> = _errorMessage

    private val _isLoading = MutableLiveData(false)
    val isLoading : LiveData<Boolean> = _isLoading

    fun login(username: String, password: String) {
        if(username.isBlank()) {
            _errorMessage.value = "Please enter your username."
            return
        }

        if(password.isBlank()) {
            _errorMessage.value = "Please enter your password."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _loginResult.value = null

            try {
                repository.addSampleDataIfEmpty()

                val user = repository.login(
                    username.trim(),
                    password
                )

                if(user == null) {
                    _errorMessage.value = "Incorrect username or password."
                } else {
                    _loginResult.value = user
                }
            } catch (e: Exception)  {
                _errorMessage.value = "Unable to log in. Please try again."
            } finally {
                _isLoading.value = false
            }
        }
    }
}