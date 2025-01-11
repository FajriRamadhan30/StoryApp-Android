package com.project.storyapp.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {
    val loginResult = MutableLiveData<LoginResponse>()
    val registerResult = MutableLiveData<RegisterResponse>()
    val errorMessage = MutableLiveData<String>()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = repository.login(email, password)
                if (!response.error) {
                    loginResult.postValue(response)
                } else {
                    errorMessage.postValue(response.message)
                }
            } catch (e: Exception) {
                errorMessage.postValue("Login failed: ${e.message}")
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = repository.register(name, email, password)
                if (!response.error) {
                    registerResult.postValue(response)
                } else {
                    errorMessage.postValue(response.message)
                }
            } catch (e: Exception) {
                errorMessage.postValue("Registration failed: ${e.message}")
            }
        }
    }
}
