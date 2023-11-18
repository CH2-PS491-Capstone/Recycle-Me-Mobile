package com.bangkit.recycleme.ui.recycling

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RecyclingViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is recycling Fragment"
    }
    val text: LiveData<String> = _text
}