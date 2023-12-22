package com.bangkit.recycleme.ui.favorite

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit.recycleme.api.ApiConfig
import com.bangkit.recycleme.di.UserRepository
import com.bangkit.recycleme.models.FavoriteArticlesItem
import com.bangkit.recycleme.models.GetFavoriteArticleResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoriteViewModel(private val repository: UserRepository) : ViewModel() {

    val storyList = MutableLiveData<List<FavoriteArticlesItem>>()
    val error = MutableLiveData<String>()

    fun loadArticle(token: String) {
        val client = ApiConfig.getApiService(token).getFavoriteArticle()
        client.enqueue(object : Callback<GetFavoriteArticleResponse> {
            override fun onResponse(call: Call<GetFavoriteArticleResponse>, response: Response<GetFavoriteArticleResponse>) {
                if (response.isSuccessful) {
                    storyList.postValue(response.body()?.favoriteArticles)
                } else {
                    error.postValue("ERROR ${response.code()} : ${response.message()}")
                }
            }

            override fun onFailure(call: Call<GetFavoriteArticleResponse>, t: Throwable) {
                Log.e(ContentValues.TAG, "onFailure Call: ${t.message}")
                error.postValue("Error : ${t.message}")
            }
        })
    }
}