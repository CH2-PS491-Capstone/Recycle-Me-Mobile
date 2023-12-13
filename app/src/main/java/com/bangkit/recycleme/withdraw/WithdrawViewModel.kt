package com.bangkit.recycleme.withdraw

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit.recycleme.api.ApiConfig
import com.bangkit.recycleme.api.WithdrawRequest
import com.bangkit.recycleme.di.UserRepository
import com.bangkit.recycleme.models.TotalResponse
import com.bangkit.recycleme.models.WithdrawResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WithdrawViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _withdraw = MutableLiveData<Int>()
    val withdraw: LiveData<Int> get() = _withdraw

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun withdrawCoin(token: String, inputCoins: Int) {
        val client = ApiConfig.getApiService(token).withdrawCoin(WithdrawRequest(inputCoins))
        client.enqueue(object : Callback<WithdrawResponse> {
            override fun onResponse(call: Call<WithdrawResponse>, response: Response<WithdrawResponse>) {
                if (response.isSuccessful) {
                    val totalResponse = response.body()
                    totalResponse?.let {
                        _withdraw.postValue(it.updateCoins ?: 0)
                    } ?: run {
                        _error.postValue("Response body is null")
                    }
                } else {
                    _error.postValue("ERROR ${response.code()} : ${response.message()}")
                }
            }

            override fun onFailure(call: Call<WithdrawResponse>, t: Throwable) {
                Log.e(ContentValues.TAG, "onFailure Call: ${t.message}")
                _error.postValue("Error : ${t.message}")
            }
        })
    }
}