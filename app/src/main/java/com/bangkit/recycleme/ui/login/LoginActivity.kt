package com.bangkit.recycleme.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.bangkit.recycleme.ui.dashboard.DashboardActivity
import com.bangkit.recycleme.models.ErrorResponse
import com.bangkit.recycleme.databinding.ActivityLoginBinding
import com.bangkit.recycleme.models.UserModel
import com.bangkit.recycleme.di.UserPreference
import com.bangkit.recycleme.factory.ViewModelFactory
import com.bangkit.recycleme.di.dataStore
import com.google.gson.Gson
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var userPreference: UserPreference
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            showLoading(true)

            viewModel.login(email, password).observe(this) { result ->
                showLoading(false)

                when (result) {
                    is Result.Success -> {
                        val token = result.data
                        Toast.makeText(this, "Login Berhasil", Toast.LENGTH_SHORT).show()

                        val userPreference = UserPreference.getInstance(this.dataStore)

                        lifecycleScope.launch {
                            userPreference.saveSession(UserModel(email, token))
                        }

                        val intent = Intent(this, DashboardActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    }

                    is Result.Error -> {
                        val errorMessage = result.error
                        try {
                            val errorBody = Gson().fromJson(errorMessage, ErrorResponse::class.java)
                            val detailedErrorMessage = errorBody.message
                            Toast.makeText(this, detailedErrorMessage, Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(this, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loadingBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}