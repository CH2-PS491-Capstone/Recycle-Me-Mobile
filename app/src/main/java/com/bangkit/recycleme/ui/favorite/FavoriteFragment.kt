package com.bangkit.recycleme.ui.favorite

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.recycleme.R
import com.bangkit.recycleme.adapter.ArticleAdapter
import com.bangkit.recycleme.adapter.FavoriteAdapter
import com.bangkit.recycleme.database.UserDatabase
import com.bangkit.recycleme.databinding.FragmentFavoriteBinding
import com.bangkit.recycleme.databinding.FragmentProfileBinding
import com.bangkit.recycleme.di.Repository
import com.bangkit.recycleme.di.UserPreference
import com.bangkit.recycleme.di.dataStore
import com.bangkit.recycleme.factory.ViewModelFactory
import com.bangkit.recycleme.models.Article
import com.bangkit.recycleme.models.ListArticlesItem
import com.bangkit.recycleme.ui.article.ArticleViewModel
import com.bangkit.recycleme.ui.detail.DetailActivity
import com.bangkit.recycleme.ui.detail.DetailViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class FavoriteFragment : Fragment() {

    private lateinit var adapter: FavoriteAdapter // Anda perlu membuat adapter sesuai kebutuhan Anda
    private val favoriteViewModel by viewModels<FavoriteViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvFavorite)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = FavoriteAdapter { view ->
            val position = recyclerView.getChildAdapterPosition(view)
            val clickedUser = adapter.getStory(position)
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra("id", clickedUser.id)
            startActivity(intent)
        }

        recyclerView.adapter = adapter

        observeViewModel()
        loadFavoriteArticles()
    }

    private fun observeViewModel() {
        favoriteViewModel.storyList.observe(viewLifecycleOwner, Observer { favoriteArticles ->
            // Update adapter with the new list of favorite articles
            adapter.setStories(favoriteArticles)
        })

        favoriteViewModel.error.observe(viewLifecycleOwner, Observer { errorMessage ->
            // Handle error message
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        })
    }

    private fun loadFavoriteArticles() {
        // Load favorite articles from the ViewModel
        // You need to pass the token to loadArticle function
        val pref = UserPreference.getInstance(requireContext().dataStore)
        val token = runBlocking { pref.getSession().first().token }
        favoriteViewModel.loadArticle(token)
    }
}

