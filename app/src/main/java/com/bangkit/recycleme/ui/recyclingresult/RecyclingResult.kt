package com.bangkit.recycleme.ui.recyclingresult

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.recycleme.ui.recycling.BridgeActivity
import com.bangkit.recycleme.R
import com.bangkit.recycleme.adapter.RecyclingAdapter
import com.bangkit.recycleme.databinding.ActivityRecyclingResultBinding
import com.bangkit.recycleme.detail.DetailRecyclingActivity
import com.bangkit.recycleme.di.UserPreference
import com.bangkit.recycleme.di.dataStore
import com.bangkit.recycleme.factory.ViewModelFactory
import com.bangkit.recycleme.ui.dashboard.DashboardActivity
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class RecyclingResult : AppCompatActivity() {
    private val viewModel by viewModels<RecyclingResultViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var recyclingAdapter: RecyclingAdapter
    private lateinit var binding: ActivityRecyclingResultBinding

    private lateinit var buttonsContainer: LinearLayout
    private lateinit var mainButton: ExtendedFloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycling_result)


        binding = ActivityRecyclingResultBinding.inflate(layoutInflater)

        initRecyclerView()
        observeViewModel()

        val pref = UserPreference.getInstance(this.dataStore)
        val token = runBlocking { pref.getSession().first().token }

        viewModel.loadRecycling(token)

        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.menu_appbar)
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        val fabAddRecycling = findViewById<ExtendedFloatingActionButton>(R.id.fabAddRecycling)
        fabAddRecycling.setOnClickListener {
            val intent = Intent(this, BridgeActivity::class.java)
            startActivity(intent)
        }


        val fabToProfile = findViewById<ExtendedFloatingActionButton>(R.id.fabToProfile)
        fabToProfile.setOnClickListener{
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        }

        buttonsContainer = findViewById(R.id.buttonsContainer)
        mainButton = findViewById(R.id.mainButton)

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun showButtons(view: View) {
        val slideLeft = AnimationUtils.loadAnimation(this, R.anim.slide_left)
        val slideRight = AnimationUtils.loadAnimation(this, R.anim.slide_right)

        if (buttonsContainer.visibility == View.VISIBLE) {
            buttonsContainer.startAnimation(slideRight)
            buttonsContainer.visibility = View.GONE
            mainButton.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                ContextCompat.getDrawable(this, R.drawable.baseline_arrow_back_ios_new_24),
                null
            )
        } else {
            buttonsContainer.startAnimation(slideLeft)
            buttonsContainer.visibility = View.VISIBLE
            mainButton.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                ContextCompat.getDrawable(this, R.drawable.baseline_arrow_forward_ios_24),
                null
            )
        }
    }

    private fun initRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.rv_recycling)
        recyclerView.layoutManager = GridLayoutManager(applicationContext, 2)
        recyclingAdapter = RecyclingAdapter(

        ) { view ->
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
            if (stories.isNullOrEmpty()) {
                showEmptyView()
            } else {
                hideEmptyView()
                recyclingAdapter.setStories(stories)
            }
        })

        viewModel.error.observe(this, Observer { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                error(errorMessage)
            }
        })
    }

    private fun showEmptyView() {
        findViewById<RecyclerView>(R.id.rv_recycling).visibility = View.GONE
        findViewById<TextView>(R.id.empty_text_view).visibility = View.VISIBLE
    }

    private fun hideEmptyView() {
        findViewById<RecyclerView>(R.id.rv_recycling).visibility = View.VISIBLE
        findViewById<TextView>(R.id.empty_text_view).visibility = View.GONE
    }
}