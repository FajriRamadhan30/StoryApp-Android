package com.project.storyapp.repository

import androidx.paging.*
import com.project.storyapp.api.ApiService
import com.project.storyapp.models.StoryItem
import com.project.storyapp.models.StoriesResponse
import com.project.storyapp.models.BaseResponse
import androidx.paging.PagingSource
import androidx.paging.PagingState
import okhttp3.MultipartBody
import okhttp3.RequestBody
import androidx.lifecycle.LiveData
import retrofit2.Response

class StoriesRepository(private val apiService: ApiService) {

    suspend fun getStoriesWithLocation(token: String): StoriesResponse {
        return apiService.getStoriesWithLocation("Bearer $token")
    }

    // Mendapatkan cerita dengan pagination menggunakan PagingSource
    fun getStoryPagingSource(token: String): PagingSource<Int, StoryItem> {
        return object : PagingSource<Int, StoryItem>() {

            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoryItem> {
                val page = params.key ?: 1 // Mulai dari halaman 1
                return try {
                    val response = apiService.getAllStories(
                        authorization = "Bearer $token",
                        page = page,
                        size = params.loadSize
                    )
                    val stories = response.listStory

                    LoadResult.Page(
                        data = stories ?: emptyList(),
                        prevKey = if (page == 1) null else page - 1,
                        nextKey = if (stories.isNullOrEmpty()) null else page + 1
                    )
                } catch (e: Exception) {
                    LoadResult.Error(e)
                }
            }

            override fun getRefreshKey(state: PagingState<Int, StoryItem>): Int? {
                return state.anchorPosition?.let { anchorPosition ->
                    state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                        ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
                }
            }
        }
    }

    // Mendapatkan data cerita dengan paging dalam bentuk Flow<PagingData<StoryItem>> yang dikembalikan sebagai LiveData
    fun getStoriesPaging(token: String): LiveData<PagingData<StoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10, // Ukuran halaman
                enablePlaceholders = false // Placeholder tidak diaktifkan
            ),
            pagingSourceFactory = { getStoryPagingSource(token) }
        ).liveData
    }


    // Menambahkan cerita baru
    suspend fun addNewStory(
        description: RequestBody,
        photo: MultipartBody.Part,
        lat: Float?,
        lon: Float?,
        token: String
    ): Response<BaseResponse> {
        return apiService.addNewStory(
            description = description,
            photo = photo,
            lat = lat,
            lon = lon,
            authorization = "Bearer $token" // Pastikan token di-attach di header Authorization
        )
    }
}
