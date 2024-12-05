package com.rifftyo.storyappdicoding.ui.detail

import androidx.lifecycle.ViewModel
import com.rifftyo.storyappdicoding.data.StoryRepository

class DetailViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun getDetailStory(id: String) = storyRepository.getDetailStory(id)
}