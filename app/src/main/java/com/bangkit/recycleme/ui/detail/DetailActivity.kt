package com.bangkit.recycleme.ui.detail

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bangkit.recycleme.models.Phone
import com.bangkit.recycleme.R
import com.bangkit.recycleme.database.UserDatabase
import com.bangkit.recycleme.databinding.ActivityDetailBinding
import com.bangkit.recycleme.di.Repository
import com.bangkit.recycleme.di.UserPreference
import com.bangkit.recycleme.di.dataStore
import com.bangkit.recycleme.factory.ViewModelFactory
import com.bangkit.recycleme.ui.recyclingresult.RecyclingResult
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class DetailActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var kategoriView: TextView
    private lateinit var judulView: TextView
    private lateinit var alatView: TextView
    private lateinit var langkahView: TextView
    private val viewModel by viewModels<DetailViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val storyId = intent.getStringExtra("id")
        val pref = UserPreference.getInstance(this.dataStore)
        val token = runBlocking { pref.getSession().first().token }
        if (storyId != null) {
            viewModel.loadStoryDetail(token, storyId)
        }

        viewModel.storyDetail.observe(this, Observer { story ->
            imageView = binding.tvPictureStory
            Glide.with(this@DetailActivity)
                .load(story?.gambar)
                .into(imageView)

            kategoriView = binding.tvKategori
            judulView = binding.tvTitleArticle
            alatView = binding.tvAlatDesc
            langkahView = binding.tvLangkahDesc

            kategoriView.text = "Kategori sampah: " + story?.jenis
            judulView.text = story?.judul
            alatView.text = story?.alatBahan
            langkahView.text = story?.langkah

            binding.fabFavoriteRemove.setOnClickListener {
                // Kode untuk menghapus artikel dari favorit
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        if (story != null) {
                            viewModel.deleteFavoriteArticle(token, story.id)
                        }
                    }

                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@DetailActivity,
                            "Artikel telah dihapus dari favorit.",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Set tampilan tombol favorit setelah penghapusan
                        if (story != null) {
                            story.isFavorite = false
                        }
                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                }
            }

            binding.fabFavoriteAdd.setOnClickListener{
                // Kode untuk menambahkan artikel ke favorit

                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        story?.let { it1 -> viewModel.addArticleToFavorites(token, it1.id) }
                    }

                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@DetailActivity,
                            "Artikel telah ditambahkan ke favorit.",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Set tampilan tombol favorit setelah penambahan
                        story?.isFavorite = true
                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                }
            }
        })
    }
}