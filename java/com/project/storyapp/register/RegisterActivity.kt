package com.project.storyapp.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.project.storyapp.R
import com.project.storyapp.api.ApiConfig
import com.project.storyapp.auth.AuthRepository
import com.project.storyapp.auth.AuthViewModel
import com.project.storyapp.auth.ViewModelFactory
import com.project.storyapp.CustomEditText
import com.project.storyapp.landing.LandingActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var authViewModel: AuthViewModel
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val repository = AuthRepository(ApiConfig.getApiService())
        val factory = ViewModelFactory(repository)
        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        progressBar = findViewById(R.id.progress_bar)

        val nameEditText = findViewById<CustomEditText>(R.id.ed_register_name)
        val emailEditText = findViewById<CustomEditText>(R.id.ed_register_email)
        val passwordEditText = findViewById<CustomEditText>(R.id.ed_register_password)
        val registerButton = findViewById<Button>(R.id.btn_register)

        // Atur tipe validasi untuk CustomEditText
        emailEditText.isEmailField = true
        passwordEditText.isPasswordField = true

        // Tambahkan animasi properti
        val logo = findViewById<View>(R.id.img_logo)
        val title = findViewById<TextView>(R.id.register_title)

        startAnimations(logo, title, nameEditText, emailEditText, passwordEditText, registerButton)

        registerButton.setOnClickListener {
            val isNameValid = nameEditText.isValid()
            val isEmailValid = emailEditText.isValid()
            val isPasswordValid = passwordEditText.isValid()

            if (isNameValid && isEmailValid && isPasswordValid) {
                showLoading(true)
                authViewModel.register(
                    nameEditText.text.toString(),
                    emailEditText.text.toString(),
                    passwordEditText.text.toString()
                )
            } else {
                Toast.makeText(this, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show()
            }
        }

        authViewModel.registerResult.observe(this) { response ->
            showLoading(false)
            if (response != null && !response.error) {
                Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                navigateToLanding()
            } else {
                Toast.makeText(this, response?.message ?: "Registration failed", Toast.LENGTH_SHORT).show()
            }
        }

        authViewModel.errorMessage.observe(this) { error ->
            showLoading(false)
            error?.let {
                Toast.makeText(this, "Error: $it", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startAnimations(
        logo: View,
        title: TextView,
        nameEditText: CustomEditText,
        emailEditText: CustomEditText,
        passwordEditText: CustomEditText,
        registerButton: Button
    ) {
        // Animasi Logo
        val logoAnimator = ObjectAnimator.ofFloat(logo, "translationY", 500f, 0f).apply {
            duration = 1000
        }

        // Animasi Title
        val titleAnimator = ObjectAnimator.ofFloat(title, "alpha", 0f, 1f).apply {
            duration = 1500
        }

        // Animasi Name EditText
        val nameAnimator = ObjectAnimator.ofFloat(nameEditText, "translationX", -500f, 0f).apply {
            duration = 1000
        }

        // Animasi Email EditText
        val emailAnimator = ObjectAnimator.ofFloat(emailEditText, "translationX", 500f, 0f).apply {
            duration = 1000
        }

        // Animasi Password EditText (Diperbaiki)
        val passwordAnimator = ObjectAnimator.ofFloat(passwordEditText, "alpha", 0f, 1f).apply {
            duration = 1200
        }

        // Animasi Button
        val buttonAnimator = ObjectAnimator.ofFloat(registerButton, "scaleX", 0.5f, 1f).apply {
            duration = 800
        }

        // Gabungkan semua animasi
        AnimatorSet().apply {
            playTogether(
                logoAnimator,
                titleAnimator,
                nameAnimator,
                emailAnimator,
                passwordAnimator,
                buttonAnimator
            )
            start()
        }
    }

    private fun navigateToLanding() {
        val intent = Intent(this, LandingActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
