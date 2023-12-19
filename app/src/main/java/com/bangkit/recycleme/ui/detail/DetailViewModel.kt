package com.bangkit.recycleme.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit.recycleme.api.ApiConfig
import com.bangkit.recycleme.di.UserRepository
import com.bangkit.recycleme.models.Article
import com.bangkit.recycleme.models.DetailArticleResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailViewModel(repository: UserRepository) : ViewModel() {
    private val _storyDetail = MutableLiveData<Article?>()
    val storyDetail: LiveData<Article?> = _storyDetail
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error


    fun loadStoryDetail(token: String, storyId: String) {
        val call = ApiConfig.getApiService(token).getDetailArticle(storyId)
        call.enqueue(object : Callback<DetailArticleResponse> {
            override fun onResponse(call: Call<DetailArticleResponse>, response: Response<DetailArticleResponse>) {
                if (response.isSuccessful) {
                    val story = response.body()?.article
                    _storyDetail.value = story
                } else {
                    val errorMessage = "Error: ${response.code()} ${response.message()}"
                    _error.value = errorMessage
                }
            }

            override fun onFailure(call: Call<DetailArticleResponse>, t: Throwable) {
                _error.value = "Failure: ${t.message}"
            }
        })
    }
}