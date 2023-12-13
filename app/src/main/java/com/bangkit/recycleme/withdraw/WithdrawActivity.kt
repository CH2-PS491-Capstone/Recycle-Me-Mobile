package com.bangkit.recycleme.withdraw

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.viewModels
import com.bangkit.recycleme.databinding.ActivityWithdrawBinding
import com.bangkit.recycleme.di.UserPreference
import com.bangkit.recycleme.di.dataStore
import com.bangkit.recycleme.factory.ViewModelFactory
import com.bangkit.recycleme.ui.dashboard.DashboardActivity
import com.bangkit.recycleme.ui.profile.ProfileViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class WithdrawActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWithdrawBinding
    private val viewModelTotal by viewModels<ProfileViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private val viewModelWithdraw by viewModels<WithdrawViewModel> {
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

        binding.buttonWithdraw.setOnClickListener {
            val totalCoins = viewModelTotal.totalCoins.value ?: 0
            val inputCoins = binding.inputCoin.text.toString().toIntOrNull() ?: 0
            val inputPhone = binding.inputPhone.text.toString()
            if (totalCoins < 10) {
                showToast("Koin tidak mencukupi. Minimum 10 koin diperlukan.")
            } else if (inputCoins > totalCoins){
                showToast("Withdraw Gagal. Koin yang kamu input melebihi koin yang kamu miliki.")
            }
            else {
                // Check if the input is valid (not null and greater than 0)
                if (inputCoins > 0) {
                    // Perform the coin withdrawal operation with the inputCoins value
                    viewModelWithdraw.withdrawCoin(token, inputCoins)
                } else {
                    showToast("Invalid input. Please enter a valid number of coins.")
                }
            }
            // Observe changes in the withdraw result and handle accordingly
            viewModelWithdraw.withdraw.observe(this) {
                showToast("Withdraw Berhasil")

                // Update the totalCoins LiveData in viewModelTotal (assuming it's a MutableLiveData)
                viewModelTotal.totalCoins.value?.let { currentTotalCoins ->
                    val updatedTotalCoins = currentTotalCoins - inputCoins
                    viewModelTotal.totalCoins.postValue(updatedTotalCoins)
                }
                val intent = Intent(this, WithdrawSuccess::class.java)
                intent.putExtra("coins", inputCoins.toString())
                intent.putExtra("phone", inputPhone)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        }

        // Observe errors from the withdraw operation
        viewModelWithdraw.error.observe(this) { error ->
            showToast("Error: $error")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
