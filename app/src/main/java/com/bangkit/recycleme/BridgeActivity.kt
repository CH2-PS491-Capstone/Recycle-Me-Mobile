package com.bangkit.recycleme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bangkit.recycleme.ui.recycling.RecyclingFragment

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