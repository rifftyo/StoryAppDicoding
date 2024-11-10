package com.rifftyo.storyappdicoding.di

import android.content.Context
import android.util.Log
import com.rifftyo.storyappdicoding.data.StoryRepository
import com.rifftyo.storyappdicoding.data.remote.retrofit.ApiConfig
import com.rifftyo.storyappdicoding.utils.UserPreferences
import com.rifftyo.storyappdicoding.utils.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        Log.d("Injection", "provideRepository: called")
        val pref = UserPreferences.getInstance(context.dataStore)
        val user = runBlocking { pref.getUserToken().first() }
        val apiService = ApiConfig.getApiService(user)
        return StoryRepository.getInstance(apiService, pref)
    }
}