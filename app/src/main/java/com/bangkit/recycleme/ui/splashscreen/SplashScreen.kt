package com.bangkit.recycleme.ui.splashscreen

import android.content.Intent
import android.os.Handler
import androidx.activity.viewModels
import androidx.core.os.postDelayed
import androidx.core.view.ViewCompat
import com.bangkit.recycleme.R
import com.bangkit.recycleme.databinding.ActivitySplashScreenBinding
import com.bangkit.recycleme.factory.ViewModelFactory
import com.bangkit.recycleme.ui.dashboard.DashboardActivity
import com.bangkit.recycleme.ui.introslider.HomeSlider
import com.bangkit.recycleme.welcome.AuthViewModel

import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.recycleme.ui.getstarted.GetStarted
import com.bangkit.recycleme.ui.login.LoginViewModel

class SplashScreen : AppCompatActivity() {

    private val viewModel by viewModels<AuthViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val splashDelay = 2000

        val videoView = findViewById<VideoView>(R.id.videoView)
        val videoUri = Uri.parse("android.resource://${packageName}/${R.raw.recycle}")
        videoView.setVideoURI(videoUri)

        // Set media controller to null to prevent it from being displayed
        videoView.setMediaController(null)

        videoView.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.isLooping = true
            mediaPlayer.start()
        }

        viewModel.getSession().observe(this) { user ->
            if (user.isLogin) {
                Handler().postDelayed({
                    val intent = Intent(this, DashboardActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }, splashDelay.toLong())
            } else {
                Handler().postDelayed({
                    val intent = Intent(this, GetStarted::class.java)
                    startActivity(intent)
                }, splashDelay.toLong())
            }
        }
    }
}