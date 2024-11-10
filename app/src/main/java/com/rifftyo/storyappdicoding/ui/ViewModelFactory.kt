package com.rifftyo.storyappdicoding.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rifftyo.storyappdicoding.data.StoryRepository
import com.rifftyo.storyappdicoding.di.Injection
import com.rifftyo.storyappdicoding.ui.detail.DetailViewModel
import com.rifftyo.storyappdicoding.ui.home.HomeViewModel
import com.rifftyo.storyappdicoding.ui.login.LoginViewModel
import com.rifftyo.storyappdicoding.ui.register.RegisterViewModel
import com.rifftyo.storyappdicoding.ui.upload.UploadViewModel

class ViewModelFactory private constructor(private val storyRepository: StoryRepository): ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(storyRepository) as T
        }
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(storyRepository) as T
        }
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(storyRepository) as T
        }
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(storyRepository) as T
        }
        if (modelClass.isAssignableFrom(UploadViewModel::class.java)) {
            return UploadViewModel(storyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository(context))
            }.also { instance = it }
    }
}