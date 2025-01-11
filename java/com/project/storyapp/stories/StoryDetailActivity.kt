package com.project.storyapp.stories

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.project.storyapp.R
import com.project.storyapp.api.ApiConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StoryDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story_detail)

        // Ambil ID story dari Intent
        val storyId = intent.getStringExtra("story_id")
        val token = getSharedPreferences("user_session", MODE_PRIVATE).getString("token", null)

        if (storyId != null && token != null) {
            fetchStoryDetails(storyId, token)
        } else {
            Toast.makeText(this, "Story ID or token not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchStoryDetails(storyId: String, token: String) {
        val nameTextView: TextView = findViewById(R.id.tv_detail_name)
        val photoImageView: ImageView = findViewById(R.id.iv_detail_photo)
        val descriptionTextView: TextView = findViewById(R.id.tv_detail_description)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Panggil API untuk mendapatkan detail story
                val apiService = ApiConfig.getApiService()
                val response = apiService.getStoryById("Bearer $token", storyId)

                withContext(Dispatchers.Main) {
                    if (!response.error) {
                        // Tampilkan data pada UI
                        val story = response.story
                        nameTextView.text = story.name
                        descriptionTextView.text = story.description
                        Glide.with(this@StoryDetailActivity)
                            .load(story.photoUrl)
                            .placeholder(R.drawable.ic_menu) // Placeholder sementara
                            .into(photoImageView)
                    } else {
                        Toast.makeText(this@StoryDetailActivity, response.message, Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@StoryDetailActivity, "Failed to fetch story details", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
