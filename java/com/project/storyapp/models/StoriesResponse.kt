package com.project.storyapp.models

data class StoriesResponse(
    val error: Boolean,
    val message: String,
    val listStory: List<StoryItem>
)

data class StoryItem(
    val id: String,
    val name: String,
    val description: String,
    val photoUrl: String,
    val createdAt: String,
    val lat: Double? = null,
    val lon: Double? = null
)
