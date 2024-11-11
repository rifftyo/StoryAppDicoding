package com.rifftyo.storyappdicoding.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rifftyo.storyappdicoding.data.Result
import com.rifftyo.storyappdicoding.data.StoryRepository
import com.rifftyo.storyappdicoding.data.remote.response.StoryResponse
import kotlinx.coroutines.launch

class HomeViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    private val _stories = MediatorLiveData<Result<StoryResponse>>()
    val stories: LiveData<Result<StoryResponse>> = _stories

    private var isDataLoaded = false

    fun getStories() {
        if (isDataLoaded) return

        viewModelScope.launch {
            _stories.value = Result.Loading
            try {
                val result = storyRepository.getStories()
                result.observeForever {
                    _stories.value = it
                    if (it is Result.Success) isDataLoaded = true
                }
            } catch (e: Exception) {
                _stories.value = Result.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun clearUserToken() {
        storyRepository.clearUserToken()
    }
}
