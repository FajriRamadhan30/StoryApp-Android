package com.project.storyapp.models

data class StoryResponse(
    val error: Boolean,         // Indikator apakah ada error
    val message: String,        // Pesan yang dikirim oleh server
    val listStory: List<StoryItem> // Daftar cerita (berbasis `StoryItem`)
)
