package com.project.storyapp.api

import com.project.storyapp.auth.LoginResponse
import com.project.storyapp.auth.RegisterResponse
import com.project.storyapp.models.StoriesResponse
import com.project.storyapp.models.BaseResponse
import com.project.storyapp.models.StoryDetailResponse
import com.project.storyapp.models.StoryResponse
import okhttp3.MultipartBody
import retrofit2.Response
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {

    // Login
    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    // Register
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    // Mendapatkan semua cerita (dengan atau tanpa pagination, dan opsi lokasi)
    @GET("stories")
    suspend fun getAllStories(
        @Header("Authorization") authorization: String, // Header untuk token
        @Query("page") page: Int? = null,               // Halaman untuk pagination
        @Query("size") size: Int? = null,               // Ukuran halaman untuk pagination
        @Query("location") location: Int? = null        // Lokasi (1 untuk true, 0 untuk false)
    ): StoriesResponse

    // Fungsi untuk menambahkan cerita baru
    @Multipart
    @POST("stories")
    suspend fun addNewStory(
        @Part("description") description: RequestBody, // Deskripsi cerita
        @Part photo: MultipartBody.Part,              // Foto cerita
        @Query("lat") lat: Float?,                    // Latitude lokasi
        @Query("lon") lon: Float?,                    // Longitude lokasi
        @Header("Authorization") authorization: String // Header untuk token
    ): Response<BaseResponse>

    // Mendapatkan cerita dengan pagination (untuk PagingSource)
    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") authorization: String, // Header untuk token
        @Query("page") page: Int,                       // Halaman saat ini
        @Query("size") size: Int                        // Ukuran halaman
    ): StoryResponse

    // Mendapatkan detail cerita berdasarkan ID
    @GET("stories/{id}")
    suspend fun getStoryById(
        @Header("Authorization") authorization: String, // Header untuk token
        @Path("id") storyId: String                     // ID cerita
    ): StoryDetailResponse

    // Mendapatkan cerita dengan lokasi (API untuk fitur lokasi)
    @GET("stories")
    suspend fun getStoriesWithLocation(
        @Header("Authorization") authorization: String, // Header untuk token
        @Query("location") location: Int = 1            // Lokasi (default 1 untuk true)
    ): StoriesResponse
}
