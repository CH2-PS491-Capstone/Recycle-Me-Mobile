package com.bangkit.recycleme.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.recycleme.api.AddFavoriteRequest
import com.bangkit.recycleme.api.ApiConfig
import com.bangkit.recycleme.api.LoginRequest
import com.bangkit.recycleme.di.UserRepository
import com.bangkit.recycleme.models.AddFavoriteResponse
import com.bangkit.recycleme.models.Article
import com.bangkit.recycleme.models.DeleteFavoriteResponse
import com.bangkit.recycleme.models.DeleteResponse
import com.bangkit.recycleme.models.DetailArticleResponse
import com.bangkit.recycleme.models.LoginResponse
import com.bangkit.recycleme.ui.login.Result
import kotlinx.coroutines.launch
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

    fun addArticleToFavorites(token: String, id: String) {
        val resultLiveData = MutableLiveData<Result<String>>()
        val apiService = ApiConfig.getApiService(token)


        val call = apiService.addArticleFavorite(AddFavoriteRequest(id))

        call.enqueue(object : Callback<AddFavoriteResponse> {
            override fun onResponse(call: Call<AddFavoriteResponse>, response: Response<AddFavoriteResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        loginResponse.message
                    } else {
                        resultLiveData.postValue(Result.Error("Response body is null"))
                    }
                } else {
                    val errorMessage = response.errorBody()?.string()
                    resultLiveData.postValue(Result.Error(errorMessage ?: "Unknown error"))
                }
            }

            override fun onFailure(call: Call<AddFavoriteResponse>, t: Throwable) {
                resultLiveData.postValue(Result.Error(t.message ?: "Network error"))
            }
        })
    }

    fun deleteFavoriteArticle(token: String, deleteId: String) {
        val call = ApiConfig.getApiService(token).deleteFavoriteArticle(deleteId)
        call.enqueue(object : Callback<DeleteFavoriteResponse> {
            override fun onResponse(call: Call<DeleteFavoriteResponse>, response: Response<DeleteFavoriteResponse>) {
                if (response.isSuccessful) {
                    // Set _recyclingDetail.value to null after successful deletion
                    _storyDetail.value = null
                } else {
                    handleErrorResponse(response)
                }
            }

            override fun onFailure(call: Call<DeleteFavoriteResponse>, t: Throwable) {
                _error.value = "Failure: ${t.message}"
            }
        })
    }

    private fun handleErrorResponse(response: Response<*>) {
        val errorMessage = "Error: ${response.code()} ${response.message()}"
        _error.value = errorMessage
    }
}