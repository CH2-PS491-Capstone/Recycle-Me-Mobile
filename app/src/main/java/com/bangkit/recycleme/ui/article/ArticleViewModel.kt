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
import com.bangkit.recycleme.di.UserRepository
import com.bangkit.recycleme.models.ArticleResponse
import com.bangkit.recycleme.models.FavoriteArticlesItem
import com.bangkit.recycleme.models.ListArticlesItem
import com.bangkit.recycleme.models.RandomArticleResponse
import com.bangkit.recycleme.models.RandomArticlesItem
import com.bangkit.recycleme.models.RekomendasiArtikel
import com.bangkit.recycleme.models.RekomendasiArtikelResponse
import com.bangkit.recycleme.models.TotalResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ArticleViewModel(repository: UserRepository) : ViewModel() {
    val storyList = MutableLiveData<List<ListArticlesItem>>()
    val rekomedasiArtikel = MutableLiveData<List<RandomArticlesItem>>()
    private val _totalFavorite = MutableLiveData<Int>()
    val totalFavorite: LiveData<Int> get() = _totalFavorite
    val error = MutableLiveData<String>()
    private val _error = MutableLiveData<String>()
    val rekomendasi = MutableLiveData<List<RekomendasiArtikel>>()
    val error2: LiveData<String> get() = _error

    private val _judulFavorite = MutableLiveData<String>()
    val judulFavorite: LiveData<String> get() = _judulFavorite

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

    fun getRekomendasiArtikel(token: String) {
        val client = ApiConfig.getApiService(token).getArtikelRekomendasi()
        client.enqueue(object : Callback<RandomArticleResponse> {
            override fun onResponse(call: Call<RandomArticleResponse>, response: Response<RandomArticleResponse>) {
                if (response.isSuccessful) {
                    rekomedasiArtikel.postValue(response.body()?.randomArticles)
                } else {
                    error.postValue("ERROR ${response.code()} : ${response.message()}")
                }
            }

            override fun onFailure(call: Call<RandomArticleResponse>, t: Throwable) {
                Log.e(ContentValues.TAG, "onFailure Call: ${t.message}")
                error.postValue("Error : ${t.message}")
            }
        })
    }

    fun loadTotalData(token: String) {
        val client = ApiConfig.getApiService(token).getTotal()
        client.enqueue(object : Callback<TotalResponse> {
            override fun onResponse(call: Call<TotalResponse>, response: Response<TotalResponse>) {
                if (response.isSuccessful) {
                    val totalResponse = response.body()
                    totalResponse?.let {
                        _totalFavorite.postValue(it.favoriteArticles ?: 0)
                    } ?: run {
                        _error.postValue("Response body is null")
                    }
                } else {
                    _error.postValue("ERROR ${response.code()} : ${response.message()}")
                }
            }

            override fun onFailure(call: Call<TotalResponse>, t: Throwable) {
                Log.e(ContentValues.TAG, "onFailure Call: ${t.message}")
                _error.postValue("Error : ${t.message}")
            }
        })
    }
}
