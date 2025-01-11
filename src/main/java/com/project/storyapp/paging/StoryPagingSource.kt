package com.project.storyapp.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.project.storyapp.api.ApiService
import com.project.storyapp.models.StoryItem

class StoriesPagingSource(
    private val apiService: ApiService,
    private val token: String
) : PagingSource<Int, StoryItem>() {

    override fun getRefreshKey(state: PagingState<Int, StoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoryItem> {
        return try {
            val position = params.key ?: 1
            val response = apiService.getAllStories(
                authorization = "Bearer $token",
                page = position,
                size = params.loadSize
            )

            LoadResult.Page(
                data = response.listStory,
                prevKey = if (position == 1) null else position - 1,
                nextKey = if (response.listStory.isEmpty()) null else position + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
