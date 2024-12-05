package com.rifftyo.storyappdicoding.ui.upload

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rifftyo.storyappdicoding.data.StoryRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UploadViewModel(private val repository: StoryRepository): ViewModel() {
    private val _imageUri = MutableLiveData<Uri?>()
    val imageUri: LiveData<Uri?> = _imageUri

    fun setImageUri(uri: Uri?) {
        _imageUri.value = uri
    }

    fun uploadStory(file: MultipartBody.Part, description: RequestBody) = repository.uploadStory(file, description)

    fun uploadStoryLocation(file: MultipartBody.Part, description: RequestBody, lat: Float, lon: Float) = repository.uploadStoryLocation(file, description, lat, lon)
}