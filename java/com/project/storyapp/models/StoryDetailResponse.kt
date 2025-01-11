package com.project.storyapp.models

data class StoryDetailResponse(
    val error: Boolean,
    val message: String,
    val story: StoryItem
)
