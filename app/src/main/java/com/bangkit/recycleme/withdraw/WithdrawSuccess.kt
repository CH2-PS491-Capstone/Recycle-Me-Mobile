package com.bangkit.recycleme.withdraw

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bangkit.recycleme.R
import com.bangkit.recycleme.databinding.ActivityWithdrawBinding
import com.bangkit.recycleme.databinding.ActivityWithdrawSuccessBinding
import com.bangkit.recycleme.ui.dashboard.DashboardActivity

class WithdrawSuccess : AppCompatActivity() {
    private lateinit var binding: ActivityWithdrawSuccessBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWithdrawSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val inputPhone = intent.getStringExtra("phone")
        val inputCoins = intent.getStringExtra("coins")

        binding.tvWithdrawSuccess.text = "Kamu telah berhasil menukarkan ${inputCoins} Koin menjadi saldo E-Wallet dengan nomor HP ${inputPhone} sebesar ${inputCoins} Rupiah."

        binding.buttonDashboard.setOnClickListener{
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        }
    }
}