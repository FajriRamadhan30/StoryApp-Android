package com.project.storyapp.landing

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.animation.OvershootInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.project.storyapp.R
import com.project.storyapp.login.LoginActivity
import com.project.storyapp.register.RegisterActivity
import com.project.storyapp.main.MainActivity

class LandingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)

        // Check if user is already logged in
        if (!token.isNullOrEmpty()) {
            navigateToMainActivity()
        } else {
            setContentView(R.layout.activity_landing)
            setupButtons()
            startAnimations()
        }
    }

    private fun setupButtons() {
        findViewById<Button>(R.id.btn_landing_login).setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btn_landing_register).setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Prevent user from returning to LandingActivity
    }

    private fun startAnimations() {
        val logo = findViewById<ImageView>(R.id.logo)
        val welcomeText = findViewById<TextView>(R.id.tv_welcome)
        val loginButton = findViewById<Button>(R.id.btn_landing_login)
        val registerButton = findViewById<Button>(R.id.btn_landing_register)

        // Logo animation (fade in and scale up)
        val logoFadeIn = ObjectAnimator.ofFloat(logo, "alpha", 0f, 1f)
        logoFadeIn.duration = 1000
        val logoScaleUpX = ObjectAnimator.ofFloat(logo, "scaleX", 0f, 1f)
        logoScaleUpX.duration = 1000
        val logoScaleUpY = ObjectAnimator.ofFloat(logo, "scaleY", 0f, 1f)
        logoScaleUpY.duration = 1000

        logoFadeIn.start()
        logoScaleUpX.start()
        logoScaleUpY.start()

        // Welcome text animation (fade in and slide up)
        val welcomeFadeIn = ObjectAnimator.ofFloat(welcomeText, "alpha", 0f, 1f)
        welcomeFadeIn.duration = 1000
        val welcomeSlideUp = ObjectAnimator.ofFloat(welcomeText, "translationY", 200f, 0f)
        welcomeSlideUp.duration = 1000

        welcomeFadeIn.start()
        welcomeSlideUp.start()

        // Buttons animation
        val loginButtonFadeIn = ObjectAnimator.ofFloat(loginButton, "alpha", 0f, 1f)
        loginButtonFadeIn.duration = 1000
        val loginButtonSlideUp = ObjectAnimator.ofFloat(loginButton, "translationY", 200f, 0f)
        loginButtonSlideUp.duration = 1000

        val registerButtonFadeIn = ObjectAnimator.ofFloat(registerButton, "alpha", 0f, 1f)
        registerButtonFadeIn.duration = 1000
        val registerButtonSlideUp = ObjectAnimator.ofFloat(registerButton, "translationY", 200f, 0f)
        registerButtonSlideUp.duration = 1000

        loginButtonFadeIn.start()
        loginButtonSlideUp.start()
        registerButtonFadeIn.start()
        registerButtonSlideUp.start()

        // Add overshoot effect
        loginButtonSlideUp.interpolator = OvershootInterpolator()
        registerButtonSlideUp.interpolator = OvershootInterpolator()
    }
}
