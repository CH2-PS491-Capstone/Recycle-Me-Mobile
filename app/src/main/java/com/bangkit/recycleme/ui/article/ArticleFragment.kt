package com.bangkit.recycleme.ui.article

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.recycleme.ui.detail.DetailActivity
import com.bangkit.recycleme.R
import com.bangkit.recycleme.adapter.ArticleAdapter
import com.bangkit.recycleme.databinding.FragmentArticleBinding
import com.bangkit.recycleme.di.UserPreference
import com.bangkit.recycleme.di.dataStore
import com.bangkit.recycleme.factory.ViewModelFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking


class ArticleFragment : Fragment() {
    private lateinit var storyAdapter: ArticleAdapter
    private val storyViewModel by viewModels<ArticleViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    private var _binding: FragmentArticleBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArticleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = binding.rvPhone
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        storyAdapter = ArticleAdapter { view ->
            val position = recyclerView.getChildAdapterPosition(view)
            val clickedUser = storyAdapter.getStory(position)
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra("id", clickedUser.id)
            startActivity(intent)
        }
        recyclerView.adapter = storyAdapter

        storyViewModel.storyList.observe(viewLifecycleOwner, Observer { stories ->
            storyAdapter.setStories(stories)
        })

        storyViewModel.error.observe(viewLifecycleOwner, Observer { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                error(errorMessage)
            }
        })

        val pref = UserPreference.getInstance(requireContext().dataStore)
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

        storyViewModel.error.observe(viewLifecycleOwner, Observer { errorMessage ->
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
        val popup = PopupMenu(requireContext(), anchor)
        popup.menuInflater.inflate(R.menu.filter_menu, popup.menu)
        val pref = UserPreference.getInstance(requireContext().dataStore)
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
        val pref = UserPreference.getInstance(requireContext().dataStore)
        val token = runBlocking { pref.getSession().first().token }
        storyViewModel.searchFilter(token, "", category)
    }
}
