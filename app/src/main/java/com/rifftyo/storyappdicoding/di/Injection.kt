package com.rifftyo.storyappdicoding.di

import android.content.Context
import android.util.Log
import com.rifftyo.storyappdicoding.data.StoryRepository
import com.rifftyo.storyappdicoding.data.local.StoryDatabase
import com.rifftyo.storyappdicoding.data.remote.retrofit.ApiConfig
import com.rifftyo.storyappdicoding.utils.UserPreferences

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        Log.d("Injection", "provideRepository: called")
        val pref = UserPreferences.getInstance(context)
        val user = pref.getUserToken()
        val apiService = ApiConfig.getApiService(user)
        val storyDatabase = StoryDatabase.getDatabase(context)
        return StoryRepository.getInstance(apiService, pref, storyDatabase)
    }
}