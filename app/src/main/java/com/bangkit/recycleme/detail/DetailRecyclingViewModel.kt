package com.bangkit.recycleme.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit.recycleme.api.ApiConfig
import com.bangkit.recycleme.di.UserRepository
import com.bangkit.recycleme.models.DeleteResponse
import com.bangkit.recycleme.models.DetailResponse
import com.bangkit.recycleme.models.Recycling
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailRecyclingViewModel(repository: UserRepository) : ViewModel() {
    private val _recyclingDetail = MutableLiveData<Recycling?>()
    val recyclingDetail: LiveData<Recycling?> = _recyclingDetail
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error


    fun loadRecyclingDetail(token: String, recyclingId: String) {
        val call = ApiConfig.getApiService(token).detailRecycling(recyclingId)
        call.enqueue(object : Callback<DetailResponse> {
            override fun onResponse(call: Call<DetailResponse>, response: Response<DetailResponse>) {
                if (response.isSuccessful) {
                    val recycling = response.body()?.recycling
                    _recyclingDetail.value = recycling
                } else {
                    val errorMessage = "Error: ${response.code()} ${response.message()}"
                    _error.value = errorMessage
                }
            }

            override fun onFailure(call: Call<DetailResponse>, t: Throwable) {
                _error.value = "Failure: ${t.message}"
            }
        })
    }

    fun deleteRecycling(token: String, deleteId: String) {
        val call = ApiConfig.getApiService(token).deleteRecycling(deleteId)
        call.enqueue(object : Callback<DeleteResponse> {
            override fun onResponse(call: Call<DeleteResponse>, response: Response<DeleteResponse>) {
                if (response.isSuccessful) {
                    // Set _recyclingDetail.value to null after successful deletion
                    _recyclingDetail.value = null
                } else {
                    handleErrorResponse(response)
                }
            }

            override fun onFailure(call: Call<DeleteResponse>, t: Throwable) {
                _error.value = "Failure: ${t.message}"
            }
        })
    }

    private fun handleErrorResponse(response: Response<*>) {
        val errorMessage = "Error: ${response.code()} ${response.message()}"
        _error.value = errorMessage
    }
}