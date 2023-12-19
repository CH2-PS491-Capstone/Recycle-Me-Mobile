package com.bangkit.recycleme.ui.article

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.recycleme.ui.detail.DetailActivity
import com.bangkit.recycleme.adapter.ListPhoneAdapter
import com.bangkit.recycleme.models.Phone
import com.bangkit.recycleme.R
import com.bangkit.recycleme.adapter.ArticleAdapter
import com.bangkit.recycleme.adapter.LoadingStateAdapter
import com.bangkit.recycleme.databinding.FragmentArticleBinding
import com.bangkit.recycleme.databinding.FragmentProfileBinding
import com.bangkit.recycleme.factory.ViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton

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
            val clickedUser = storyAdapter.getArticle(position)
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra("id", clickedUser?.id)
            startActivity(intent)
        }
        storyViewModel.article.observe(viewLifecycleOwner) {
            storyAdapter.submitData(lifecycle, it)
        }

        recyclerView.adapter = storyAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                storyAdapter.retry()
            }
        )

        storyViewModel.error.observe(viewLifecycleOwner, Observer { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                error(errorMessage)
            }
        })
    }

    private fun showFilterMenu(anchor: View) {
        val popup = PopupMenu(requireContext(), anchor)
        popup.menuInflater.inflate(R.menu.filter_menu, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            // Handle item click here
            when (item.itemId) {
                R.id.menu_filter_1 -> filterListByCategory("Elektronik (B3)")
                R.id.menu_filter_2 -> filterListByCategory("Kaca (Anorganik)")
                // Add more filters as needed
            }
            true
        }

        popup.show()
    }

    private fun filterListByCategory(category: String) {
//        storyViewModel.setFilterCategory(category)
    }
}
