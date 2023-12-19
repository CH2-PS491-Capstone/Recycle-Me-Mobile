package com.bangkit.recycleme.ui.article

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bangkit.recycleme.api.ApiConfig
import com.bangkit.recycleme.api.ApiService
import com.bangkit.recycleme.di.UserRepository
import com.bangkit.recycleme.models.ArticleResponse
import com.bangkit.recycleme.models.ListArticlesItem
import com.bangkit.recycleme.models.ListRecyclingItem
import com.bangkit.recycleme.models.RecyclingResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ArticleViewModel(repository: UserRepository) : ViewModel() {
    val storyList = MutableLiveData<List<ListArticlesItem>>()
    val error = MutableLiveData<String>()

    fun loadArticle(token: String) {
        val client = ApiConfig.getApiService(token).getArticle()
        client.enqueue(object : Callback<ArticleResponse> {
            override fun onResponse(call: Call<ArticleResponse>, response: Response<ArticleResponse>) {
                if (response.isSuccessful) {
                    storyList.postValue(response.body()?.listArticles)
                } else {
                    error.postValue("ERROR ${response.code()} : ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ArticleResponse>, t: Throwable) {
                Log.e(ContentValues.TAG, "onFailure Call: ${t.message}")
                error.postValue("Error : ${t.message}")
            }
        })
    }

    fun searchFilter(token: String, search: String, jenis: String) {
        val client = ApiConfig.getApiService(token).getArticle(search, jenis)
        client.enqueue(object : Callback<ArticleResponse> {
            override fun onResponse(call: Call<ArticleResponse>, response: Response<ArticleResponse>) {
                if (response.isSuccessful) {
                    storyList.postValue(response.body()?.listArticles)
                } else {
                    error.postValue("ERROR ${response.code()} : ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ArticleResponse>, t: Throwable) {
                Log.e(ContentValues.TAG, "onFailure Call: ${t.message}")
                error.postValue("Error : ${t.message}")
            }
        })
    }
}
