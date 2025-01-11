package com.project.storyapp

import com.project.storyapp.models.StoryItem

object DataDummy {
    fun generateDummyStories(): List<StoryItem> {
        val stories = mutableListOf<StoryItem>()
        for (i in 1..10) {
            stories.add(
                StoryItem(
                    id = i.toString(),
                    name = "Story $i",
                    description = "Description $i",
                    photoUrl = "https://example.com/photo$i.jpg",
                    createdAt = "2023-01-01T00:00:00Z"
                )
            )
        }
        return stories
    }
}
