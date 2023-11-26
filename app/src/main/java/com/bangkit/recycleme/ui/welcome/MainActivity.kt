package com.bangkit.recycleme.ui.welcome

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.bangkit.recycleme.databinding.ActivityMainBinding
import com.bangkit.recycleme.factory.ViewModelFactory
import com.bangkit.recycleme.ui.dashboard.DashboardActivity
import com.bangkit.recycleme.ui.login.LoginActivity
import com.bangkit.recycleme.ui.register.RegisterActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<AuthViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onStart() {
        super.onStart()
        viewModel.getSession().observe(this) { user ->
            if (user.isLogin) {
                val intent = Intent(this, DashboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.btnSignup.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}