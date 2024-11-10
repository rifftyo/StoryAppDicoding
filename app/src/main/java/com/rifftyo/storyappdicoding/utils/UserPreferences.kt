package com.rifftyo.storyappdicoding.utils

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferences private constructor(private val dataStore: DataStore<Preferences>){

    private val USER_TOKEN = stringPreferencesKey("user_token")

    fun getUserToken(): Flow<String> {
        Log.d("UserPreferences", "getUserToken called")
        return dataStore.data.map { preferences ->
            preferences[USER_TOKEN] ?: ""
        }
    }

    suspend fun saveUserToken(token: String) {
        Log.d("UserPreferences", "saveUserToken called with token: $token")
        dataStore.edit { preferences ->
            preferences[USER_TOKEN] = token
        }
    }

    suspend fun clearUserToken() {
        dataStore.edit { preferences ->
            preferences.remove(USER_TOKEN)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): UserPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }


}