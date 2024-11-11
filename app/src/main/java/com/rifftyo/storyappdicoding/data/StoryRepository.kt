package com.rifftyo.storyappdicoding.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.google.gson.Gson
import com.rifftyo.storyappdicoding.data.remote.response.DetailResponse
import com.rifftyo.storyappdicoding.data.remote.response.LoginResponse
import com.rifftyo.storyappdicoding.data.remote.response.RegisterResponse
import com.rifftyo.storyappdicoding.data.remote.response.StoryResponse
import com.rifftyo.storyappdicoding.data.remote.response.UploadResponse
import com.rifftyo.storyappdicoding.data.remote.retrofit.ApiConfig
import com.rifftyo.storyappdicoding.data.remote.retrofit.ApiService
import com.rifftyo.storyappdicoding.utils.UserPreferences
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

class StoryRepository private constructor(
    private var apiService: ApiService,
    private val userPreferences: UserPreferences
){
    private val resultRegister = MediatorLiveData<Result<RegisterResponse>>()
    private val resultLogin = MediatorLiveData<Result<LoginResponse>>()
    private val resultStory = MediatorLiveData<Result<StoryResponse>>()
    private val resultDetail = MediatorLiveData<Result<DetailResponse>>()
    private val resultUpload = MediatorLiveData<Result<UploadResponse>>()

    suspend fun registerUser(name: String, email: String, password: String): LiveData<Result<RegisterResponse>> {
        try {
            resultRegister.value = Result.Loading
            val response = apiService.registerUser(name, email, password)
            resultRegister.value = Result.Success(response)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, RegisterResponse::class.java)
            resultRegister.value = Result.Error(errorResponse.message.toString())
            Log.d("StoryRepository", resultRegister.value.toString())
        } catch (e: Exception) {
            resultRegister.value = Result.Error(e.message.toString())
            Log.d("StoryRepository", "registerUser: ${e.message.toString()}")
        }
        return resultRegister
    }

    suspend fun loginUser(email: String, password: String): LiveData<Result<LoginResponse>> {
        try {
            resultLogin.value = Result.Loading
            val response = apiService.loginUser(email, password)
            resultLogin.value = Result.Success(response)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, LoginResponse::class.java)
            resultLogin.value = Result.Error(errorResponse.message.toString())
            Log.d("StoryRepository", resultLogin.value.toString())
        } catch (e: Exception) {
            resultLogin.value = Result.Error(e.message.toString())
            Log.d("StoryRepository", "loginUser: ${e.message.toString()}")
        }
        return resultLogin
    }

    suspend fun getDetailStory(id: String): LiveData<Result<DetailResponse>> {
        try {
            resultDetail.value = Result.Loading
            val response = apiService.getDetailStory(id)
            resultDetail.value = Result.Success(response)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, DetailResponse::class.java)
            resultDetail.value = Result.Error(errorResponse.message.toString())
        } catch (e: Exception) {
            resultDetail.value = Result.Error(e.message.toString())
            Log.d("StoryRepository", "getDetailStory: ${e.message.toString()}")
        }
        return resultDetail
    }

    suspend fun uploadStory(description: MultipartBody.Part, imageFile: RequestBody): LiveData<Result<UploadResponse>> {
        try {
            resultUpload.value = Result.Loading
            val response = apiService.uploadStory(description, imageFile)
            resultUpload.value = Result.Success(response)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, UploadResponse::class.java)
            resultUpload.value = Result.Error(errorResponse.message.toString())
        } catch (e: Exception) {
            resultUpload.value = Result.Error(e.message.toString())
            Log.d("StoryRepository", "uploadStory: ${e.message.toString()}")
        }
        return resultUpload
    }

    fun clearUserToken() {
        userPreferences.clearUserToken()
    }

    fun saveUserToken(token: String) {
        userPreferences.saveUserToken(token)
        apiService = ApiConfig.getApiService(token)
    }

    suspend fun getStories(): LiveData<Result<StoryResponse>> {
        Log.d("StoryRepository", "getStories: called")
        try {
            resultStory.value = Result.Loading
            val response = apiService.getStories()
            resultStory.value = Result.Success(response)
        } catch (e: HttpException) {

            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, StoryResponse::class.java)
            resultStory.value = Result.Error(errorResponse.message.toString())
            Log.d("StoryRepository", resultStory.value.toString())
        } catch (e: Exception) {
            resultStory.value = Result.Error(e.message.toString())
            Log.d("StoryRepository", "getStories: ${e.message.toString()}")
        }
        return resultStory
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiService,
            userPreferences: UserPreferences
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, userPreferences)
            }.also { instance = it }
    }
}