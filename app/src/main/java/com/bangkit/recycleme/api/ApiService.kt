package com.bangkit.recycleme.api

import com.bangkit.recycleme.models.LoginResponse
import com.bangkit.recycleme.models.RegisterResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class LoginRequest(
    val email: String,
    val password: String,
)

interface ApiService {
    @POST("auth/register")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>
}