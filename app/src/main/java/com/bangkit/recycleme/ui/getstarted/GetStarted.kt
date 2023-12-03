package com.bangkit.recycleme.ui.getstarted

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bangkit.recycleme.R
import com.bangkit.recycleme.databinding.ActivityGetStartedBinding
import com.bangkit.recycleme.ui.introslider.HomeSlider
import com.bangkit.recycleme.ui.login.LoginActivity

class GetStarted : AppCompatActivity() {
    private lateinit var binding: ActivityGetStartedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGetStartedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonStarted.setOnClickListener{
            val intent = Intent(this, HomeSlider::class.java)
            startActivity(intent)
        }
    }
}