package com.bangkit.recycleme.ui.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.bangkit.recycleme.models.ErrorResponse
import com.bangkit.recycleme.databinding.ActivityRegisterBinding
import com.bangkit.recycleme.factory.ViewModelFactory
import com.bangkit.recycleme.ui.login.LoginActivity
import com.google.gson.Gson

class RegisterActivity : AppCompatActivity() {
    private val viewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signupButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val name = binding.edRegisterName.text.toString()
            val password = binding.passwordEditText.text.toString()
            showLoading(true)

            viewModel.register(name, email, password) { success, errorMessage ->
                showLoading(false)

                if (success) {
                    Toast.makeText(this, "Register Success", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    try {
                        val errorBody = Gson().fromJson(errorMessage, ErrorResponse::class.java)
                        val detailedErrorMessage = errorBody.message
                        Toast.makeText(this, detailedErrorMessage, Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(this, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loadingBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}