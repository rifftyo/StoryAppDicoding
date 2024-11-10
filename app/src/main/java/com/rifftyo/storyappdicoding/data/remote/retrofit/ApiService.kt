package com.rifftyo.storyappdicoding.data.remote.retrofit

import com.rifftyo.storyappdicoding.data.remote.response.DetailResponse
import com.rifftyo.storyappdicoding.data.remote.response.LoginResponse
import com.rifftyo.storyappdicoding.data.remote.response.RegisterResponse
import com.rifftyo.storyappdicoding.data.remote.response.StoryResponse
import com.rifftyo.storyappdicoding.data.remote.response.UploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun registerUser(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("stories")
    suspend fun getStories(): StoryResponse

    @GET("stories/{id}")
    suspend fun getDetailStory(
        @Path("id") id: String
    ): DetailResponse

    @Multipart
    @POST("stories")
    suspend fun uploadStory(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): UploadResponse
}