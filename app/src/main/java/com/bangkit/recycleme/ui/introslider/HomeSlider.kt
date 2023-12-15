package com.bangkit.recycleme.ui.introslider

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.viewpager2.widget.ViewPager2
import com.bangkit.recycleme.R
import com.bangkit.recycleme.adapter.IntroSliderAdapter
import com.bangkit.recycleme.databinding.ActivityHomeSliderBinding
import com.bangkit.recycleme.welcome.MainActivity

class HomeSlider : AppCompatActivity() {

    private lateinit var binding: ActivityHomeSliderBinding
    private lateinit var introSliderViewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeSliderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val introSliderAdapter = IntroSliderAdapter(
            listOf(
                IntroSlide(
                    getString(R.string.recyclingArticle),
                    getString(R.string.recyclingArticleDesc),
                    R.drawable.articlewaste
                ),
                IntroSlide(
                    getString(R.string.scanTrash),
                    getString(R.string.scanTrashDesc),
                    R.drawable.scanwaste
                ),
                IntroSlide(
                    getString(R.string.levelUp),
                    getString(R.string.levelUpDesc),
                    R.drawable.wastestore
                )
            )
        )

        introSliderViewPager = binding.viewPager
        introSliderViewPager.adapter = introSliderAdapter

        val indicators = Array(introSliderAdapter.itemCount) {
            ImageView(this)
        }
        val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(8, 0, 8, 0)
        for (i in indicators.indices) {
            indicators[i].apply {
                setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive
                    )
                )
                this.layoutParams = layoutParams
            }
            binding.indicatorsContainer.addView(indicators[i])
        }

        setCurrentIndicator(0)
        introSliderViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
            }
        )
        binding.buttonNext.setOnClickListener{
            if (introSliderViewPager.currentItem + 1 < introSliderAdapter.itemCount) {
                introSliderViewPager.currentItem += 1
            } else {
                Intent(applicationContext, MainActivity::class.java).also {
                    startActivity(it)
                }
            }
        }

        binding.textSkipIntro.setOnClickListener{
            Intent(applicationContext, MainActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }
    }

    private fun setupIndicators() {

    }

    private fun setCurrentIndicator(index: Int) {
        val childCount = binding.indicatorsContainer.childCount
        for (i in 0 until childCount) {
            val imageView = binding.indicatorsContainer[i] as ImageView
            if (i == index) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_active
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive
                    )
                )
            }
        }
    }

}