package com.bangkit.recycleme.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit.recycleme.models.LoginResponse
import com.bangkit.recycleme.api.ApiConfig
import com.bangkit.recycleme.api.LoginRequest
import com.bangkit.recycleme.di.UserRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val repository: UserRepository) : ViewModel() {

    fun login(email: String, password: String): LiveData<Result<String>> {
        val resultLiveData = MutableLiveData<Result<String>>()
        val apiService = ApiConfig.getApiService("")


        val call = apiService.login(LoginRequest(email, password))

        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        val token = loginResponse.loginResult?.token
                        if (token != null) {
                            resultLiveData.postValue(Result.Success(token))
                        } else {
                            resultLiveData.postValue(Result.Error("Empty response"))
                        }
                    } else {
                        resultLiveData.postValue(Result.Error("Response body is null"))
                    }
                } else {
                    val errorMessage = response.errorBody()?.string()
                    resultLiveData.postValue(Result.Error(errorMessage ?: "Unknown error"))
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                resultLiveData.postValue(Result.Error(t.message ?: "Network error"))
            }
        })

        return resultLiveData
    }
}