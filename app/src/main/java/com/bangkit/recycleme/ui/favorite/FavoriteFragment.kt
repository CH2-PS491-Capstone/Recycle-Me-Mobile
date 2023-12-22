package com.bangkit.recycleme.ui.favorite

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.recycleme.R
import com.bangkit.recycleme.adapter.FavoriteAdapter
import com.bangkit.recycleme.di.UserPreference
import com.bangkit.recycleme.di.dataStore
import com.bangkit.recycleme.factory.ViewModelFactory
import com.bangkit.recycleme.ui.detail.DetailActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

const val DETAIL_ACTIVITY_REQUEST_CODE = 1001
class FavoriteFragment : Fragment() {

    private lateinit var adapter: FavoriteAdapter
    private val favoriteViewModel by viewModels<FavoriteViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    private lateinit var noFavoriteTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noFavoriteTextView = view.findViewById(R.id.noFavoriteTextView)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvFavorite)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = FavoriteAdapter { view ->
            val position = recyclerView.getChildAdapterPosition(view)
            val clickedUser = adapter.getStory(position)
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra("id", clickedUser.id)
            startActivityForResult(intent, DETAIL_ACTIVITY_REQUEST_CODE)
        }

        recyclerView.adapter = adapter

        observeViewModel()
        loadFavoriteArticles()
    }

    private fun observeViewModel() {
        favoriteViewModel.storyList.observe(viewLifecycleOwner, Observer { favoriteArticles ->
            adapter.setStories(favoriteArticles)

            // Show/hide the TextView based on the list of favorite users
            if (favoriteArticles.isEmpty()) {
                noFavoriteTextView.visibility = View.VISIBLE
            } else {
                noFavoriteTextView.visibility = View.GONE
            }
        })

        favoriteViewModel.error.observe(viewLifecycleOwner, Observer { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        })
    }

    private fun loadFavoriteArticles() {
        val pref = UserPreference.getInstance(requireContext().dataStore)
        val token = runBlocking { pref.getSession().first().token }
        favoriteViewModel.loadArticle(token)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == DETAIL_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            loadFavoriteArticles()
        }
    }
}

