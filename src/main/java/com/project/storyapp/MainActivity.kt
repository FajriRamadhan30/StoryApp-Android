package com.project.storyapp.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.project.storyapp.R
import com.project.storyapp.adapter.StoriesAdapter
import com.project.storyapp.api.ApiConfig
import com.project.storyapp.landing.LandingActivity
import com.project.storyapp.maps.MapsActivity
import com.project.storyapp.models.StoryItem
import com.project.storyapp.repository.StoriesRepository
import com.project.storyapp.stories.AddStoryActivity
import com.project.storyapp.stories.StoryDetailActivity
import com.project.storyapp.viewmodel.StoriesViewModel
import com.project.storyapp.viewmodel.StoriesViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StoriesAdapter
    private val storiesViewModel: StoriesViewModel by viewModels {
        val apiService = ApiConfig.getApiService()
        val repository = StoriesRepository(apiService)
        StoriesViewModelFactory(repository)
    }

    private var token: String? = null

    companion object {
        private const val REQUEST_CODE_ADD_STORY = 1002
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Views
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout)
        recyclerView = findViewById(R.id.recycler_view_stories)
        setSupportActionBar(toolbar)

        recyclerView.layoutManager = LinearLayoutManager(this)

        // Get token from shared preferences
        val sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
        token = sharedPreferences.getString("token", null)

        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Token not found. Please log in.", Toast.LENGTH_SHORT).show()
            logoutUser()
            return
        }

        // Initialize Adapter
        adapter = StoriesAdapter { story -> navigateToDetail(story) }
        recyclerView.adapter = adapter

        // Set custom ItemAnimator for RecyclerView
        recyclerView.itemAnimator = SlideInLeftAnimator()

        // Observe adapter's load state to control progress bar visibility
        adapter.addLoadStateListener { loadState ->
            swipeRefreshLayout.isRefreshing = loadState.refresh is LoadState.Loading

            if (loadState.refresh is LoadState.Error) {
                val error = (loadState.refresh as LoadState.Error).error
                Toast.makeText(this, "Error: ${error.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }

        // Swipe to Refresh
        swipeRefreshLayout.setOnRefreshListener {
            adapter.refresh()
        }

        // Fetch stories on start
        fetchStories()
    }

    private fun fetchStories() {
        // Collect PagingData in a coroutine
        lifecycleScope.launch {
            storiesViewModel.getStories(token!!).observe(this@MainActivity) { pagingData ->
                adapter.submitData(lifecycle, pagingData)
            }
        }

        // Observe error messages
        storiesViewModel.errorMessage.observe(this) { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun navigateToDetail(story: StoryItem) {
        val intent = Intent(this, StoryDetailActivity::class.java)
        intent.putExtra("story_id", story.id)
        startActivity(intent)
    }

    private fun navigateToAddStory() {
        val intent = Intent(this, AddStoryActivity::class.java)
        startActivityForResult(intent, REQUEST_CODE_ADD_STORY)
    }

    private fun navigateToMaps() {
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ADD_STORY && resultCode == RESULT_OK) {
            fetchStories() // Refresh the stories after adding a new one
        }
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add_story -> {
                navigateToAddStory()
                true
            }
            R.id.action_maps -> {
                navigateToMaps()
                true
            }
            R.id.action_logout -> {
                logoutUser()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logoutUser() {
        val sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()

        Toast.makeText(this, "Logged out successfully.", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, LandingActivity::class.java)
        startActivity(intent)
        finish()
    }

    // Override onBackPressed to exit the app directly
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()  // This will close the app completely
    }

    // Custom ItemAnimator for RecyclerView with slide-in animation
    class SlideInLeftAnimator : DefaultItemAnimator() {
        override fun animateAdd(holder: RecyclerView.ViewHolder?): Boolean {
            val itemView = holder?.itemView ?: return false

            // Set slide-in animation from the left
            val animation = TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, -1f,  // Start position: off-screen to the left
                Animation.RELATIVE_TO_PARENT, 0f,   // End position: on-screen
                Animation.RELATIVE_TO_PARENT, 0f,   // No vertical movement
                Animation.RELATIVE_TO_PARENT, 0f    // No horizontal movement
            )
            animation.duration = 500  // Duration of the animation in milliseconds
            itemView.startAnimation(animation)

            return super.animateAdd(holder)
        }
    }
}
