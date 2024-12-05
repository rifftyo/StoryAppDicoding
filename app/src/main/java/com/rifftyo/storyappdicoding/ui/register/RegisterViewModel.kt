package com.rifftyo.storyappdicoding.ui.register

import androidx.lifecycle.ViewModel
import com.rifftyo.storyappdicoding.data.StoryRepository

class RegisterViewModel(private val storyRepository: StoryRepository): ViewModel() {
    fun registerUser(name: String, email: String, password: String) = storyRepository.registerUser(name, email, password)
}