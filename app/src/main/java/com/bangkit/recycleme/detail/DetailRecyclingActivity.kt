package com.bangkit.recycleme.detail

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.bangkit.recycleme.R
import com.bangkit.recycleme.databinding.ActivityDashboardBinding
import com.bangkit.recycleme.databinding.ActivityDetailRecyclingBinding
import com.bangkit.recycleme.databinding.ActivityRecyclingResultBinding
import com.bangkit.recycleme.di.UserPreference
import com.bangkit.recycleme.di.dataStore
import com.bangkit.recycleme.factory.ViewModelFactory
import com.bangkit.recycleme.ui.recyclingresult.RecyclingResult
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

        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.details_menu_appbar)
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

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

        binding.fabDeleteRecycling.setOnClickListener{
            showPopUp()
        }
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

    private fun showPopUp() {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Hapus Daur Ulang")
        builder.setMessage("Apa kamu yakin mau menghapus daur ulang ini? Jika iya, koin kamu akan berkurang.")

        builder.setPositiveButton("Ya") { _, _ ->
            val deleteId = intent.getStringExtra("id")
            val pref = UserPreference.getInstance(this.dataStore)
            val token = runBlocking { pref.getSession().first().token }

            if (deleteId != null) {
                viewModel.deleteRecycling(token, deleteId)
                val message = "Daur Ulang Berhasil Dihapus"
                showToast(message)
                val intent = Intent(this, RecyclingResult::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        }

        builder.setNegativeButton("Batal") { cancel, _ ->
            cancel.dismiss()
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}