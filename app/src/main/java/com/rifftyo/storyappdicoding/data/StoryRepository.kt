package com.rifftyo.storyappdicoding.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.google.gson.Gson
import com.rifftyo.storyappdicoding.data.local.StoryDatabase
import com.rifftyo.storyappdicoding.data.local.story.StoryEntity
import com.rifftyo.storyappdicoding.data.remote.response.DetailResponse
import com.rifftyo.storyappdicoding.data.remote.response.LoginResponse
import com.rifftyo.storyappdicoding.data.remote.response.RegisterResponse
import com.rifftyo.storyappdicoding.data.remote.response.StoryResponse
import com.rifftyo.storyappdicoding.data.remote.response.UploadResponse
import com.rifftyo.storyappdicoding.data.remote.retrofit.ApiConfig
import com.rifftyo.storyappdicoding.data.remote.retrofit.ApiService
import com.rifftyo.storyappdicoding.utils.EspressoIdlingResource
import com.rifftyo.storyappdicoding.utils.UserPreferences
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

class StoryRepository private constructor(
    private var apiService: ApiService,
    private val userPreferences: UserPreferences,
    private val storyDatabase: StoryDatabase
){

    fun registerUser(name: String, email: String, password: String): LiveData<Result<RegisterResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.registerUser(name, email, password)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, RegisterResponse::class.java)
            emit(Result.Error(errorResponse.message.toString()))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun loginUser(email: String, password: String): LiveData<Result<LoginResponse>> = liveData{
        emit(Result.Loading)
        try {
            val response = apiService.loginUser(email, password)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, LoginResponse::class.java)
            emit(Result.Error(errorResponse.message.toString()))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getDetailStory(id: String): LiveData<Result<DetailResponse>> = liveData{
        try {
            val response = apiService.getDetailStory(id)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, DetailResponse::class.java)
            emit(Result.Error(errorResponse.message.toString()))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun uploadStory(description: MultipartBody.Part, imageFile: RequestBody): LiveData<Result<UploadResponse>> = liveData{
        emit(Result.Loading)
        try {
            val response = apiService.uploadStory(description, imageFile)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, UploadResponse::class.java)
            emit(Result.Error(errorResponse.message.toString()))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        } finally {
            EspressoIdlingResource.decrement()
        }
    }

    fun uploadStoryLocation(description: MultipartBody.Part, imageFile: RequestBody, lat: Float, lon: Float): LiveData<Result<UploadResponse>> = liveData{
        emit(Result.Loading)
        try {
            val response = apiService.uploadStoryLocation(description, imageFile, lat, lon)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, UploadResponse::class.java)
            emit(Result.Error(errorResponse.message.toString()))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        } finally {
            EspressoIdlingResource.decrement()
        }
    }

    fun clearUserToken() {
        userPreferences.clearUserToken()
    }

    fun saveUserToken(token: String) {
        userPreferences.saveUserToken(token)
        apiService = ApiConfig.getApiService(token)
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getStories(): LiveData<PagingData<StoryEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }

    fun getStoriesWithLocation(): LiveData<Result<StoryResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getStoriesWithLocation()
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, StoryResponse::class.java)
            emit(Result.Error(errorResponse.message.toString()))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiService,
            userPreferences: UserPreferences,
            storyDatabase: StoryDatabase
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, userPreferences, storyDatabase)
            }.also { instance = it }
    }
}