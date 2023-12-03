package com.bangkit.recycleme.detail

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.bangkit.recycleme.R
import com.bangkit.recycleme.databinding.ActivityDashboardBinding
import com.bangkit.recycleme.databinding.ActivityDetailRecyclingBinding
import com.bangkit.recycleme.databinding.ActivityRecyclingResultBinding
import com.bangkit.recycleme.di.UserPreference
import com.bangkit.recycleme.di.dataStore
import com.bangkit.recycleme.factory.ViewModelFactory
import com.bumptech.glide.Glide
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class DetailRecyclingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailRecyclingBinding

    private val viewModel by viewModels<DetailRecyclingViewModel> {
        ViewModelFactory.getInstance(this)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailRecyclingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recyclingId = intent.getStringExtra("id")
        val pref = UserPreference.getInstance(this.dataStore)
        val token = runBlocking { pref.getSession().first().token }
        if (recyclingId != null) {
            viewModel.loadRecyclingDetail(token, recyclingId)
        }

        viewModel.recyclingDetail.observe(this, Observer { recycling ->
            Glide.with(this@DetailRecyclingActivity)
                .load(recycling?.photoUrl)
                .into(binding.tvPictureStory)

            binding.tvNamaBarang.text = recycling?.barang
            binding.tvKategori.text = recycling?.kategori
            binding.tvRecycling.text = recycling?.recycling
            binding.tvNote.text = recycling?.description
            binding.tvCoins.text = "+" + recycling?.coins.toString()
        })
    }
}