package com.bangkit.recycleme.ui.article

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.recycleme.R
import com.bangkit.recycleme.adapter.ArticleAdapter
import com.bangkit.recycleme.adapter.RekomendasiAdapter
import com.bangkit.recycleme.databinding.ActivityDashboardBinding
import com.bangkit.recycleme.databinding.ActivityRekomendasiBinding
import com.bangkit.recycleme.di.UserPreference
import com.bangkit.recycleme.di.dataStore
import com.bangkit.recycleme.factory.ViewModelFactory
import com.bangkit.recycleme.ui.detail.DetailActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class RekomendasiActivity : AppCompatActivity() {
    private lateinit var storyAdapter: ArticleAdapter
    private lateinit var rekomendasiAdapter: RekomendasiAdapter
    private lateinit var wrapRekomendasi: LinearLayout
    private val storyViewModel by viewModels<ArticleViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityRekomendasiBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRekomendasiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val rekomendasi = binding.rvRekomendasi
        rekomendasi.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        storyAdapter = ArticleAdapter { view ->
            val position = rekomendasi.getChildAdapterPosition(view)
            val clickedUser = storyAdapter.getStory(position)
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("id", clickedUser.id)
            startActivity(intent)
        }

        storyViewModel.storyList.observe(this, Observer { rekomendasiArticles ->
            val randomRekomendasi = rekomendasiArticles.shuffled().take(5)
            storyAdapter.setStories(randomRekomendasi)
        })

        rekomendasi.adapter = storyAdapter

        val judul = "Keranjang dari Kertas Koran Bekas" // replace with the actual article title

        val recyclerView = binding.rvPhone
        recyclerView.layoutManager = LinearLayoutManager(this)
        storyAdapter = ArticleAdapter { view ->
            val position = recyclerView.getChildAdapterPosition(view)
            val clickedUser = storyAdapter.getStory(position)
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("id", clickedUser.id)
            startActivity(intent)
        }
        recyclerView.adapter = storyAdapter

        storyViewModel.storyList.observe(this, Observer { stories ->
            storyAdapter.setStories(stories)
        })

        storyViewModel.error.observe(this, Observer { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                error(errorMessage)
            }
        })

        val pref = UserPreference.getInstance(this.dataStore)
        val token = runBlocking { pref.getSession().first().token }

        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                storyViewModel.searchFilter(token, query ?: "", "")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                storyViewModel.loadArticle(token)
                return true
            }
        })

        storyViewModel.loadArticle(token)

        storyViewModel.error.observe(this, Observer { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                error(errorMessage)
            }
        })

        val filterButton = binding.buttonFilter
        filterButton.setOnClickListener {
            showFilterMenu(filterButton)
        }

        val scrollView = binding.nestedScrollView
        val floatingActionButton = binding.scrollTop

        // Atur listener untuk menggulir ke atas saat tombol menggulir ke atas diklik
        floatingActionButton.setOnClickListener {
            scrollView.smoothScrollTo(0, 0)
        }

        // Atur listener untuk menampilkan atau menyembunyikan tombol menggulir ke atas
        scrollView.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            if (scrollY > oldScrollY) {
                // Scroll ke bawah, sembunyikan tombol
                floatingActionButton.hide()
            } else {
                // Scroll ke atas atau tidak berubah, tampilkan tombol
                floatingActionButton.show()
            }
        }
    }

    private fun showFilterMenu(anchor: View) {
        val popup = PopupMenu(this, anchor)
        popup.menuInflater.inflate(R.menu.filter_menu, popup.menu)
        val pref = UserPreference.getInstance(this.dataStore)
        val token = runBlocking { pref.getSession().first().token }

        popup.setOnMenuItemClickListener { item ->
            // Handle item click here
            when (item.itemId) {
                R.id.menu_filter_1 -> filterListByCategory("elektronik")
                R.id.menu_filter_2 -> filterListByCategory("kaca")
                R.id.menu_filter_3 -> filterListByCategory("kain")
                R.id.menu_filter_4 -> filterListByCategory("kardus")
                R.id.menu_filter_5 -> filterListByCategory("logam")
                R.id.menu_filter_6 -> filterListByCategory("organik")
                R.id.menu_filter_7 -> filterListByCategory("plastik")
                R.id.menu_filter_8 -> filterListByCategory("kertas")
                R.id.menu_filter_9 -> storyViewModel.loadArticle(token)
            }
            true
        }

        popup.show()
    }

    private fun filterListByCategory(category: String) {
        val pref = UserPreference.getInstance(this.dataStore)
        val token = runBlocking { pref.getSession().first().token }
        storyViewModel.searchFilter(token, "", category)
    }
}