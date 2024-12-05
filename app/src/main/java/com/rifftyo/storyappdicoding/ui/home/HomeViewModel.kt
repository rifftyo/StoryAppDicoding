package com.rifftyo.storyappdicoding.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rifftyo.storyappdicoding.data.StoryRepository
import com.rifftyo.storyappdicoding.data.local.story.StoryEntity

class HomeViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    val stories: LiveData<PagingData<StoryEntity>> =
        storyRepository.getStories().cachedIn(viewModelScope)

    fun clearUserToken() {
        storyRepository.clearUserToken()
    }
}
