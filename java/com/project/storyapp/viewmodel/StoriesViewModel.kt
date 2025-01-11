package com.project.storyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.project.storyapp.models.StoryItem
import com.project.storyapp.repository.StoriesRepository

class StoriesViewModel(private val repository: StoriesRepository) : ViewModel() {

    // LiveData untuk menyimpan PagingData stories
    private val _stories = MutableLiveData<PagingData<StoryItem>>()
    val stories: LiveData<PagingData<StoryItem>> = _stories

    // LiveData untuk menampilkan pesan error
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    // Mendapatkan stories secara paging dan mengembalikan Flow yang bisa dipantau
    fun getStories(token: String): LiveData<PagingData<StoryItem>> {
        return repository.getStoriesPaging(token)
    }
}
