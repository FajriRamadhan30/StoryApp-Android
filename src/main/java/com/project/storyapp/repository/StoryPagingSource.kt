package com.project.storyapp.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.project.storyapp.api.ApiService
import com.project.storyapp.models.StoryItem

class StoriesPagingSource(
    private val apiService: ApiService,
    private val token: String
) : PagingSource<Int, StoryItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoryItem> {
        return try {
            val page = params.key ?: 1 // Mulai dari halaman 1
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
