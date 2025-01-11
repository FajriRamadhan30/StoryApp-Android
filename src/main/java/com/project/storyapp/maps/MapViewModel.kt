package com.project.storyapp.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.storyapp.models.StoryItem
import com.project.storyapp.repository.StoriesRepository
import kotlinx.coroutines.launch

class MapViewModel(private val repository: StoriesRepository) : ViewModel() {

    private val _stories = MutableLiveData<List<StoryItem>>()
    val stories: LiveData<List<StoryItem>> get() = _stories

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun getStoriesWithLocation(token: String) {
        viewModelScope.launch {
            try {
                // Assuming repository has a method getStoriesWithLocation that fetches data.
                val response = repository.getStoriesWithLocation(token)
                if (!response.error) {
                    _stories.postValue(response.listStory)
                } else {
                    _error.postValue(response.message)
                }
            } catch (e: Exception) {
                _error.postValue(e.message ?: "Error occurred")
            }
        }
    }
}
