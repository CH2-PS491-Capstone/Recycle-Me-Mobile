package com.bangkit.recycleme.ui.recycling

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bangkit.recycleme.R

class BridgeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bridge)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, RecyclingFragment())
                .commit()
        }
    }
}