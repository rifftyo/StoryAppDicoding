package com.rifftyo.storyappdicoding.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.rifftyo.storyappdicoding.R
import com.rifftyo.storyappdicoding.ui.home.HomeActivity
import com.rifftyo.storyappdicoding.utils.UserPreferences
import com.rifftyo.storyappdicoding.utils.dataStore
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        userPreferences = UserPreferences.getInstance(dataStore)
        checkLoginStatus()

        supportActionBar?.hide()
    }

    private fun checkLoginStatus() {
        lifecycleScope.launch {
            userPreferences.getUserToken().collect { token ->
                if (token.isNotEmpty()) {
                    val intentHome = Intent(this@SplashActivity, HomeActivity::class.java)
                    startActivity(intentHome)
                    finish()
                } else {
                    val intentLogin = Intent(this@SplashActivity, MainActivity::class.java)
                    startActivity(intentLogin)
                    finish()
                }
            }
        }
    }
}