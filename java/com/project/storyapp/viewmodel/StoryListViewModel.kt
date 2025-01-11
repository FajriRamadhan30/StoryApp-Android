//package com.project.storyapp.viewmodel
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import androidx.paging.PagingData
//import androidx.paging.cachedIn
//import com.project.storyapp.models.StoryItem
//import com.project.storyapp.repository.StoriesRepository
//import kotlinx.coroutines.flow.Flow
//
//class StoryListViewModel(private val repository: StoriesRepository) : ViewModel() {
//
//    fun getStoriesPaging(token: String): Flow<PagingData<StoryItem>> {
//        return repository.getStoriesPaging(token).cachedIn(viewModelScope)
//    }
//}
