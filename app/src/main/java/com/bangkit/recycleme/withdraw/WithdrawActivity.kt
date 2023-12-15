package com.bangkit.recycleme.withdraw

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.bangkit.recycleme.R
import com.bangkit.recycleme.databinding.ActivityWithdrawBinding
import com.bangkit.recycleme.di.UserPreference
import com.bangkit.recycleme.di.dataStore
import com.bangkit.recycleme.factory.ViewModelFactory
import com.bangkit.recycleme.ui.profile.ProfileViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.text.NumberFormat
import java.util.Locale

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

        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.scan_menu_appbar)
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        viewModelTotal.loadTotalData(token)

        viewModelTotal.totalCoins.observe(this) { coins ->
            // Memformat totalCoins untuk memisahkan ribuan
            val formattedTotalCoins = NumberFormat.getNumberInstance(Locale.getDefault()).format(coins)

            // Menetapkan teks ke TextView dengan format "xxx,xxx Koin"
            binding.tvCoin.text = "$formattedTotalCoins Koin"

            if (coins < 10000) {
                binding.tvRedNotifCoin.visibility = View.VISIBLE
            } else {
                binding.tvRedNotifCoin.visibility = View.GONE
            }
        }

        viewModelTotal.error.observe(this) { error ->
            Toast.makeText(this, "Error: $error", Toast.LENGTH_SHORT).show()
        }


        binding.buttonWithdraw.setOnClickListener {
            val totalCoins = viewModelTotal.totalCoins.value ?: 0
            val inputCoins = binding.inputCoin.text.toString().toIntOrNull() ?: 0
            val inputPhone = binding.inputPhone.text.toString()
            if (inputCoins < 10000) {
                showToast("Minimum koin tidak mencukupi untuk withdraw. Mohon lihat lagi persyaratannya.")
            } else if (inputCoins > totalCoins){
                showToast("Withdraw Gagal. Koin yang kamu input melebihi koin yang kamu miliki.")
            }
            else {
                if (inputCoins >= 10000 && inputPhone.isNotEmpty()) {
                    viewModelWithdraw.withdrawCoin(token, inputCoins)
                } else {
                    showToast("Form No HP wajib diisi")
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
