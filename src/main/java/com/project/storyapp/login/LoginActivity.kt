package com.project.storyapp.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.project.storyapp.R
import com.project.storyapp.api.ApiConfig
import com.project.storyapp.auth.AuthRepository
import com.project.storyapp.auth.AuthViewModel
import com.project.storyapp.auth.ViewModelFactory
import com.project.storyapp.main.MainActivity
import com.project.storyapp.CustomEditText

class LoginActivity : AppCompatActivity() {

    private lateinit var authViewModel: AuthViewModel
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val repository = AuthRepository(ApiConfig.getApiService())
        val factory = ViewModelFactory(repository)
        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        progressBar = findViewById(R.id.progress_bar)

        val emailEditText = findViewById<CustomEditText>(R.id.ed_login_email)
        val passwordEditText = findViewById<CustomEditText>(R.id.ed_login_password)
        val loginButton = findViewById<Button>(R.id.btn_login)

        emailEditText.isEmailField = true
        passwordEditText.isPasswordField = true

        val logo = findViewById<View>(R.id.img_logo)

        startAnimations(logo, emailEditText, passwordEditText, loginButton)

        loginButton.setOnClickListener {
            val isEmailValid = emailEditText.isValid()
            val isPasswordValid = passwordEditText.isValid()

            if (isEmailValid && isPasswordValid) {
                showLoading(true)
                val email = emailEditText.text.toString()
                val password = passwordEditText.text.toString()
                authViewModel.login(email, password)
            } else {
                Toast.makeText(this, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show()
            }
        }

        authViewModel.loginResult.observe(this) { response ->
            showLoading(false)
            if (response != null && !response.error) {
                saveSession(response.loginResult!!.name, response.loginResult.token)
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                navigateToMain()
            } else {
                Toast.makeText(this, response?.message ?: "Login failed", Toast.LENGTH_SHORT).show()
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
        emailEditText: CustomEditText,
        passwordEditText: CustomEditText,
        loginButton: Button
    ) {
        // Animasi Logo
        val logoAnimator = ObjectAnimator.ofFloat(logo, "translationY", 500f, 0f).apply {
            duration = 1000
        }

        // Animasi Email EditText
        val emailAnimator = ObjectAnimator.ofFloat(emailEditText, "translationX", -500f, 0f).apply {
            duration = 1000
        }

        // Animasi Password EditText
        val passwordAnimator = ObjectAnimator.ofFloat(passwordEditText, "translationX", 500f, 0f).apply {
            duration = 1000
        }

        // Animasi Button
        val buttonAnimator = ObjectAnimator.ofFloat(loginButton, "scaleX", 0.5f, 1f).apply {
            duration = 800
        }

        // Gabungkan semua animasi
        AnimatorSet().apply {
            playTogether(
                logoAnimator,
                emailAnimator,
                passwordAnimator,
                buttonAnimator
            )
            start()
        }
    }

    private fun saveSession(name: String, token: String) {
        val sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
        sharedPreferences.edit()
            .putString("name", name)
            .putString("token", token)
            .apply()
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
