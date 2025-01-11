package com.project.storyapp.maps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.project.storyapp.repository.StoriesRepository

class MapViewModelFactory(private val repository: StoriesRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MapViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
