package com.bangkit.recycleme.ui.recyclingresult

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.recycleme.R
import com.bangkit.recycleme.adapter.RecyclingAdapter
import com.bangkit.recycleme.databinding.ActivityRecyclingResultBinding
import com.bangkit.recycleme.detail.DetailRecyclingActivity
import com.bangkit.recycleme.di.UserPreference
import com.bangkit.recycleme.di.dataStore
import com.bangkit.recycleme.factory.ViewModelFactory
import com.bangkit.recycleme.ui.recycling.RecyclingViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class RecyclingResult : AppCompatActivity() {
    private val viewModel by viewModels<RecyclingResultViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var recyclingAdapter: RecyclingAdapter
    private lateinit var binding: ActivityRecyclingResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycling_result)


        binding = ActivityRecyclingResultBinding.inflate(layoutInflater)

        initRecyclerView()
        observeViewModel()

        val pref = UserPreference.getInstance(this.dataStore)
        val token = runBlocking { pref.getSession().first().token }

        viewModel.loadRecycling(token)

//        val fabAddStory = findViewById<FloatingActionButton>(R.id.fabAddStory)
//        fabAddStory.setOnClickListener {
//            val intent = Intent(this, AddStoryActivity::class.java)
//            startActivityForResult(intent, ADD_STORY_REQUEST_CODE)
//        }
    }

    companion object {
        private const val ADD_STORY_REQUEST_CODE = 1
    }

    private fun initRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.rv_recycling)
        recyclerView.layoutManager = GridLayoutManager(applicationContext, 2)
        recyclingAdapter = RecyclingAdapter() { view ->
            val position = recyclerView.getChildAdapterPosition(view)
            val clickedUser = recyclingAdapter.getStory(position)
            val intent = Intent(this, DetailRecyclingActivity::class.java)
            intent.putExtra("id", clickedUser.id)
            startActivity(intent)
        }
        recyclerView.adapter = recyclingAdapter
    }


    private fun observeViewModel() {
        viewModel.storyList.observe(this, Observer { stories ->
            stories?.let {
                recyclingAdapter.setStories(stories)
            }
        })

        viewModel.error.observe(this, Observer { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                error(errorMessage)
            }
        })
    }
}