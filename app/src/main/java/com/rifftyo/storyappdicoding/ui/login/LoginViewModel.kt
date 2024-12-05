package com.rifftyo.storyappdicoding.ui.login

import androidx.lifecycle.ViewModel
import com.rifftyo.storyappdicoding.data.StoryRepository

class LoginViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun loginUser(email: String, password: String) = storyRepository.loginUser(email,password)

    fun saveUserToken(token: String) {
        storyRepository.saveUserToken(token)
    }
}