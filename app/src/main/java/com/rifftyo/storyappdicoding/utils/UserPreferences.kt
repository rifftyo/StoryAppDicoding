package com.rifftyo.storyappdicoding.utils

import android.content.Context

class UserPreferences private constructor(context: Context) {

    companion object {
        private const val PREFS_NAME = "user_pref"
        private const val USER_TOKEN = "user_token"

        @Volatile
        private var INSTANCE: UserPreferences? = null

        fun getInstance(context: Context): UserPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreferences(context)
                INSTANCE = instance
                instance
            }
        }
    }

    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveUserToken(token: String) {
        preferences.edit().putString(USER_TOKEN, token).apply()
    }

    fun getUserToken(): String {
        return preferences.getString(USER_TOKEN, "") ?: ""
    }

    fun clearUserToken() {
        preferences.edit().remove(USER_TOKEN).apply()
    }
}