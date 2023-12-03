package com.bangkit.recycleme.api

import com.bangkit.recycleme.models.LoginResponse
import com.bangkit.recycleme.models.RecyclingAddResponse
import com.bangkit.recycleme.models.RecyclingResponse
import com.bangkit.recycleme.models.RegisterResponse
import com.bangkit.recycleme.ui.recyclingresult.RecyclingResult
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

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

    @Multipart
    @POST("recycling/add")
    fun uploadRecycling(
        @Part file: MultipartBody.Part,
        @Part("barang") barang: RequestBody,
        @Part("kategori") kategori: RequestBody,
        @Part("recycling") recycling: RequestBody,
        @Part("description") description: RequestBody
    ): Call<RecyclingAddResponse>

    @GET("recycling/results")
    fun getRecycling(): Call<RecyclingResponse>
}