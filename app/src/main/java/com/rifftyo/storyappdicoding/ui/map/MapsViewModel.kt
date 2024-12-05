package com.rifftyo.storyappdicoding.ui.map

import androidx.lifecycle.ViewModel
import com.rifftyo.storyappdicoding.data.StoryRepository

class MapsViewModel(private val storyRepository: StoryRepository): ViewModel() {
    fun getStoriesWithLocation() = storyRepository.getStoriesWithLocation()
}