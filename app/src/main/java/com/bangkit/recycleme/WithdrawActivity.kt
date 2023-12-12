package com.bangkit.recycleme

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.viewModels
import com.bangkit.recycleme.databinding.ActivityWithdrawBinding
import com.bangkit.recycleme.di.UserPreference
import com.bangkit.recycleme.di.dataStore
import com.bangkit.recycleme.factory.ViewModelFactory
import com.bangkit.recycleme.ui.profile.ProfileViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class WithdrawActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWithdrawBinding
    private val viewModelTotal by viewModels<ProfileViewModel> {
        ViewModelFactory.getInstance(this)
    }
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWithdrawBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = UserPreference.getInstance(this.dataStore)
        val token = runBlocking { pref.getSession().first().token }

        viewModelTotal.loadTotalData(token)

        viewModelTotal.totalCoins.observe(this) { totalCoins ->
            binding.tvCoin.text = "$totalCoins Koin"
        }

        viewModelTotal.error.observe(this) { error ->
            Toast.makeText(this, "Error: $error", Toast.LENGTH_SHORT).show()
        }

        binding.buttonWithdraw.setOnClickListener{
            val totalCoins = viewModelTotal.totalCoins.value ?: 0
            if(totalCoins <= 50) {
                showToast("Koin tidak mencukupi. Minimum 1000 koin diperlukan.")
            } else {
                showToast("Selamat. Koin kamu sudah berhasil ditukarkan")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}