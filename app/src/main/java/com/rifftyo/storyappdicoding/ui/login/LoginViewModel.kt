package com.rifftyo.storyappdicoding.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rifftyo.storyappdicoding.data.Result
import com.rifftyo.storyappdicoding.data.StoryRepository
import com.rifftyo.storyappdicoding.data.remote.response.LoginResponse
import kotlinx.coroutines.launch

class LoginViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    private val _loginResult = MediatorLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> get() = _loginResult

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            _loginResult.value = Result.Loading
            try {
                val result = storyRepository.loginUser(email, password)
                result.observeForever { _loginResult.value = it }
            } catch (e: Exception) {
                _loginResult.value = Result.Error(e.message ?: "Unknown error")
            }
        }
    }
}