package com.bangkit.recycleme.withdraw

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bangkit.recycleme.R
import com.bangkit.recycleme.databinding.ActivityWithdrawBinding
import com.bangkit.recycleme.databinding.ActivityWithdrawSuccessBinding
import com.bangkit.recycleme.ui.dashboard.DashboardActivity
import java.text.NumberFormat
import java.util.Locale

class WithdrawSuccess : AppCompatActivity() {
    private lateinit var binding: ActivityWithdrawSuccessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWithdrawSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val inputPhone = intent.getStringExtra("phone")
        val inputCoins = intent.getStringExtra("coins")

        binding.tvWithdrawSuccess.text =
            "Kamu telah berhasil menukarkan koin menjadi saldo E-Wallet dengan detail sebagai berikut:"

        // Memformat inputCoins untuk memisahkan ribuan
        val formattedCoins = formatCurrency(inputCoins)
        binding.tvCoin.text = "Rp. $formattedCoins"

        binding.tvDescCoin.text = "Sudah masuk ke $inputPhone"

        binding.buttonDashboard.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        }
    }

    private fun formatCurrency(amount: String?): String {
        val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())
        return try {
            val parsedAmount = amount?.toDouble() ?: 0.0
            numberFormat.format(parsedAmount)
        } catch (e: NumberFormatException) {
            "0"
        }
    }
}