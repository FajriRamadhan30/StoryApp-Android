//package com.project.storyapp.viewmodel
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import com.project.storyapp.repository.StoriesRepository
//
//class StoryListViewModelFactory(private val repository: StoriesRepository) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(StoryListViewModel::class.java)) {
//            @Suppress("UNCHECKED_CAST")
//            return StoryListViewModel(repository) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}
