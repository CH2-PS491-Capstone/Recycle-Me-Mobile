package com.bangkit.recycleme.ui.profile

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit.recycleme.api.ApiConfig
import com.bangkit.recycleme.di.UserRepository
import com.bangkit.recycleme.models.RecyclingResponse
import com.bangkit.recycleme.models.TotalResponse
import com.bangkit.recycleme.models.WithdrawResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _totalCoins = MutableLiveData<Int>()
    val totalCoins: MutableLiveData<Int> get() = _totalCoins

    private val _totalRecycling = MutableLiveData<Int>()
    val totalRecycling: LiveData<Int> get() = _totalRecycling

    private val _name = MutableLiveData<String>()
    val name: LiveData<String> get() = _name

    private val _totalWithdraw = MutableLiveData<Int>()
    val totalWithdraw: LiveData<Int> get() = _totalWithdraw

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun loadTotalData(token: String) {
        val client = ApiConfig.getApiService(token).getTotal()
        client.enqueue(object : Callback<TotalResponse> {
            override fun onResponse(call: Call<TotalResponse>, response: Response<TotalResponse>) {
                if (response.isSuccessful) {
                    val totalResponse = response.body()
                    totalResponse?.let {
                        _name.postValue(it.name ?: "N/A")
//                        _totalWithdraw.postValue(it.withdraw ?: 0)
                        _totalCoins.postValue(it.coins ?: 0)
                        _totalRecycling.postValue(it.listRecycling ?: 0)
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
