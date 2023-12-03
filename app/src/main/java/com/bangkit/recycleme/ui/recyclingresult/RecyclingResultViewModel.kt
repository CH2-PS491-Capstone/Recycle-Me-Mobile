package com.bangkit.recycleme.ui.recyclingresult

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit.recycleme.api.ApiConfig
import com.bangkit.recycleme.di.UserRepository
import com.bangkit.recycleme.models.ListRecyclingItem
import com.bangkit.recycleme.models.RecyclingResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecyclingResultViewModel(repository: UserRepository) : ViewModel() {
    val storyList = MutableLiveData<List<ListRecyclingItem>>()
    val error = MutableLiveData<String>()

    fun loadRecycling(token: String) {
        val client = ApiConfig.getApiService(token).getRecycling()
        client.enqueue(object : Callback<RecyclingResponse> {
            override fun onResponse(call: Call<RecyclingResponse>, response: Response<RecyclingResponse>) {
                if (response.isSuccessful) {
                    storyList.postValue(response.body()?.listRecycling)
                } else {
                    error.postValue("ERROR ${response.code()} : ${response.message()}")
                }
            }

            override fun onFailure(call: Call<RecyclingResponse>, t: Throwable) {
                Log.e(TAG, "onFailure Call: ${t.message}")
                error.postValue("Error : ${t.message}")
            }
        })
    }
}