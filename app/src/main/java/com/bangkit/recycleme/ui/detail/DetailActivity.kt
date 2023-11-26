package com.bangkit.recycleme.ui.detail

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.bangkit.recycleme.models.Phone
import com.bangkit.recycleme.R

class DetailActivity : AppCompatActivity() {
    private lateinit var tvDetailName: TextView
    private lateinit var tvDescription: TextView
    private lateinit var imgPhoto: ImageView
    private lateinit var tvPrice: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        tvDetailName = findViewById(R.id.tv_detail_name)
        tvDescription = findViewById(R.id.tv_detail_description)
        imgPhoto = findViewById(R.id.img_item_photo)
        tvPrice = findViewById(R.id.tv_item_price)

        val receivedData = if (Build.VERSION.SDK_INT >= 33) {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<Phone>("DATA")
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<Phone>("DATA")
        }
        if (receivedData != null) {
            supportActionBar?.title = "Detail"
            val photo = receivedData.photo
            val text = receivedData.name
            val deskripsi = receivedData.detailDescription
            val price = receivedData.price

            imgPhoto.setImageResource(photo)
            tvDetailName.text = text
            tvDescription.text = deskripsi
            tvPrice.text = "Mulai Dari: $price"
        }
    }
}