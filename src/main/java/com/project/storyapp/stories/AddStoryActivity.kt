package com.project.storyapp.stories

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.project.storyapp.main.MainActivity
import com.project.storyapp.R
import com.project.storyapp.api.ApiConfig
import com.project.storyapp.repository.StoriesRepository
import com.project.storyapp.login.LoginActivity
import com.project.storyapp.utils.FileUtil
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File

class AddStoryActivity : AppCompatActivity() {

    private lateinit var imagePreview: ImageView
    private lateinit var descriptionEditText: EditText
    private lateinit var progressBar: ProgressBar
    private var selectedImageUri: Uri? = null

    companion object {
        private const val REQUEST_CODE_GALLERY = 1001
        private const val SELECTED_IMAGE_URI = "selected_image_uri"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_story)

        // Initialize Views
        imagePreview = findViewById(R.id.iv_add_story_preview)
        descriptionEditText = findViewById(R.id.et_add_story_description)
        progressBar = findViewById(R.id.progress_bar)
        val galleryButton: Button = findViewById(R.id.btn_gallery)
        val uploadButton: Button = findViewById(R.id.btn_upload_story)

        if (savedInstanceState != null) {
            selectedImageUri = savedInstanceState.getParcelable(SELECTED_IMAGE_URI)
            selectedImageUri?.let {
                imagePreview.setImageURI(it)
            }
        }

        galleryButton.setOnClickListener {
            animateButton(galleryButton)
            openGallery()
        }

        uploadButton.setOnClickListener {
            animateButton(uploadButton)

            val description = descriptionEditText.text.toString()
            val token = getTokenFromPreferences()

            if (description.isNotEmpty() && selectedImageUri != null && token != null) {
                uploadStory(description, token)
            } else {
                Toast.makeText(
                    this,
                    "Please add an image, description, and ensure you are logged in.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            imagePreview.setImageURI(selectedImageUri)

            ObjectAnimator.ofPropertyValuesHolder(
                imagePreview,
                PropertyValuesHolder.ofFloat("scaleX", 0.7f, 1f),
                PropertyValuesHolder.ofFloat("scaleY", 0.7f, 1f),
                PropertyValuesHolder.ofFloat("alpha", 0f, 1f)
            ).apply {
                duration = 800
                start()
            }
        }
    }

    private fun animateButton(button: Button) {
        ObjectAnimator.ofPropertyValuesHolder(
            button,
            PropertyValuesHolder.ofFloat("scaleX", 1f, 1.1f, 1f),
            PropertyValuesHolder.ofFloat("scaleY", 1f, 1.1f, 1f)
        ).apply {
            duration = 300
            start()
        }
    }

    private fun uploadStory(description: String, token: String) {
        val apiService = ApiConfig.getApiService()
        val repository = StoriesRepository(apiService)

        val originalFile = selectedImageUri?.let { uri ->
            FileUtil.uriToFile(uri, this)
        } ?: run {
            Toast.makeText(this, "Failed to process image file.", Toast.LENGTH_SHORT).show()
            return
        }

        val compressedFile = compressImage(originalFile)
        if (compressedFile.length() > 1 * 1024 * 1024) {
            Toast.makeText(this, "File size exceeds 1 MB. Please choose a smaller image.", Toast.LENGTH_SHORT).show()
            return
        }

        val requestImageFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), compressedFile)
        val imageMultipart = MultipartBody.Part.createFormData("photo", compressedFile.name, requestImageFile)
        val descriptionRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), description)

        progressBar.visibility = ProgressBar.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (token.isBlank()) {
                    withContext(Dispatchers.Main) {
                        progressBar.visibility = ProgressBar.GONE
                        Toast.makeText(this@AddStoryActivity, "Token is missing or invalid. Please log in again.", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                val response = repository.addNewStory(
                    descriptionRequestBody,
                    imageMultipart,
                    null, // No location data is sent now
                    null, // No location data is sent now
                    token
                )

                withContext(Dispatchers.Main) {
                    progressBar.visibility = ProgressBar.GONE
                    if (response.isSuccessful && response.body()?.error == false) {
                        Toast.makeText(this@AddStoryActivity, "Story uploaded successfully!", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@AddStoryActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        if (response.body()?.message?.contains("Unauthorized", ignoreCase = true) == true) {
                            Toast.makeText(this@AddStoryActivity, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@AddStoryActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@AddStoryActivity, "Failed: ${response.body()?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = ProgressBar.GONE
                    Toast.makeText(this@AddStoryActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun compressImage(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.path)
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
        val compressedFile = File(cacheDir, "compressed_${file.name}")
        compressedFile.writeBytes(outputStream.toByteArray())
        return compressedFile
    }

    private fun getTokenFromPreferences(): String? {
        val sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE)
        return sharedPreferences.getString("token", null)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(SELECTED_IMAGE_URI, selectedImageUri)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        selectedImageUri = savedInstanceState.getParcelable(SELECTED_IMAGE_URI)
        selectedImageUri?.let {
            imagePreview.setImageURI(it)
        }
    }
}
